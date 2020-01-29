package me.keeleyhoek.bastion.server;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author khoek
 */
public class DBManager {

    static EntityManagerFactory EM;

    public static EntityManager createEntityManager() {
        if (EM == null) {
            throw new IllegalStateException("DBManager has not been initialised!");
        }
        return EM.createEntityManager();
    }

    public static class FieldValue {

        private final String field;
        private final Object value;

        public FieldValue(String field, Object value) {
            this.field = field;
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public Object getValue() {
            return value;
        }
    }

    public static <T extends Serializable> TypedQuery<T> buildObjectByFieldQuery(EntityManager em, Class<T> clazz, FieldValue... idents) {
        CriteriaBuilder b = em.getCriteriaBuilder();
        CriteriaQuery<T> c = b.createQuery(clazz);
        Root<T> root = c.from(clazz);
        c.select(root);
        for (FieldValue i : idents) {
            c.where(b.equal(root.get(i.getField()), i.getValue()));
        }

        return em.createQuery(c);
    }

    public static void checkAccess(RequestContext r, Protected p) throws ParameterException {
        if (!p.canAccess(r)) {
            throw new AccessDisallowedException();
        }
    }

    public static <T extends Serializable> List<T> getObjectsByFields(RequestContext r, Class<T> clazz, FieldValue... idents) {
        try {
            List<T> res = buildObjectByFieldQuery(r.getEM(), clazz, idents).getResultList();
            if (clazz.isAssignableFrom(Protected.class)) {
                for (T t : res) {
                    checkAccess(r, (Protected) t);
                }
            }
            return res;
        } catch (NoResultException e) {
            return null;
        } catch (Exception ex) {
            throw new Error("Could not get " + clazz.getName(), ex);
        }
    }

    public static <T extends Serializable> T getObjectByFields(RequestContext r, Class<T> clazz, FieldValue... idents) {
        try {
            T res = buildObjectByFieldQuery(r.getEM(), clazz, idents).getSingleResult();
            if (clazz.isAssignableFrom(Protected.class)) {
                checkAccess(r, (Protected) res);
            }
            return res;
        } catch (NoResultException e) {
            return null;
        } catch (Exception ex) {
            throw new Error("Could not get " + clazz.getName(), ex);
        }
    }

    public static <T extends Serializable> T getObjectByField(RequestContext r, Class<T> clazz, String field, Object value) {
        return getObjectByFields(r, clazz, new FieldValue(field, value));
    }

    public static <T extends Serializable> T getObjectById(RequestContext r, Class<T> clazz, int id) {
        return getObjectByField(r, clazz, "id", id);
    }

    public static <T extends Serializable> List<T> getObjects(RequestContext r, Class<T> clazz) {
        return getObjectsByFields(r, clazz);
    }
}
