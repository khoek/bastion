package me.keeleyhoek.bastion.server;

import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.EntityManager;
import me.keeleyhoek.bastion.ObjectTranscoder;
import me.keeleyhoek.bastion.Response;
import me.keeleyhoek.bastion.Validatable;
import me.keeleyhoek.bastion.server.objects.Ident;
import me.keeleyhoek.bastion.server.objects.Token;

/**
 *
 * @author khoek
 */
public class RequestContext {
    
    static RequestParser PARSER;

    public static RequestContext begin(ParameterProvider params) {        
        EntityManager em = DBManager.createEntityManager();
        em.getTransaction().begin();

        return new RequestContext(em, params);
    }

    private final ParameterProvider params;
    private final EntityManager em;

    private RequestContext(EntityManager em, ParameterProvider params) {
        this.em = em;
        this.params = Objects.requireNonNull(params);
    }

    public EntityManager getEM() {
        return em;
    }  
    
    private final HashMap<Class, Object> objs = new HashMap<>();

    public <T> T get(Class<T> clazz) throws ParameterException {
        Object o = objs.get(clazz);
        if (o == null) {
            o = lookupInternal(clazz);
            objs.put(clazz, o);
        }
        if (o == null) {
            throw new RuntimeException("null get request from parser");
        }
        return (T) o;
    }

    private Object lookupInternal(Class clazz) throws ParameterException {
        if (clazz == Ident.class) {
            Token t = lookupByField(Token.class, "token", "raw");
            if (t.getExpiry() < System.currentTimeMillis()) {
                em.remove(t);
            }
            if (t.getIdent() == null) {
                throw new ParameterException("token", Response.Status.BAD_TOKEN);
            }
            return t.getIdent();
        }
        
        return PARSER.lookup(this, clazz);
    }

    public <T> T maybeGetParam(Class<T> clazz, String key) throws IllegalParameterException {
        return maybeGetParam((Type) clazz, key);
    }

    public <T> T maybeGetParam(Type t, String key) throws IllegalParameterException {
        String raw = params.getRawParameter(key);
        if (raw == null) {
            return null;
        }

        T obj = ObjectTranscoder.decodeObject(raw, t);

        if (obj instanceof Validatable) {
            if (!((Validatable) obj).isValid()) {
                obj = null;
            }
        }

        if (obj == null) {
            throw new IllegalParameterException(key);
        }

        return obj;
    }

    public <T extends Serializable> T maybeLookupByField(Class<T> clazz, String fieldKey, String field)
            throws IllegalParameterException {
        String value = maybeGetParam(String.class, fieldKey);

        if (value == null) {
            return null;
        }

        return DBManager.getObjectByField(this, clazz, field, value);
    }

    public <T extends Serializable> T maybeLookupById(Class<T> clazz, String idKey) throws IllegalParameterException {
        return maybeLookupByField(clazz, idKey, "id");
    }

    public <T> T getParam(Class<T> clazz, String key) throws MissingParameterException, IllegalParameterException {
        return getParam((Type) clazz, key);
    }

    public <T> T getParam(Type t, String key) throws MissingParameterException, IllegalParameterException {
        T o = maybeGetParam(t, key);

        if (o == null) {
            throw new MissingParameterException(key);
        }

        return o;
    }

    public <T extends Serializable> T lookupByField(Class<T> clazz, String fieldKey, String field)
            throws MissingParameterException, IllegalParameterException {
        T o = maybeLookupByField(clazz, fieldKey, field);

        if (o == null) {
            throw new MissingParameterException(fieldKey);
        }

        return o;
    }

    public <T extends Serializable> T lookupById(Class<T> clazz, String idKey) throws MissingParameterException, IllegalParameterException {
        T o = maybeLookupById(clazz, idKey);

        if (o == null) {
            throw new MissingParameterException(idKey);
        }

        return o;
    }

    public <T extends Serializable> Set<T> lookupByIds(Class<T> clazz, String idsKey) throws MissingParameterException, IllegalParameterException {
        HashSet<T> s = new HashSet<>();
        for (Integer id : (Set<Integer>) getParam(new TypeToken<Set<Integer>>() {}.getType(), idsKey)) {
            s.add(DBManager.getObjectById(this, clazz, id));
        }
        return s;
    }

    public void abort() {
        em.getTransaction().rollback();
        em.close();
    }

    public void end() {
        em.getTransaction().commit();        
        em.close();
    }
}
