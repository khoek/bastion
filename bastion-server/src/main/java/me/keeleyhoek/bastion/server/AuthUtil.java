package me.keeleyhoek.bastion.server;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.persistence.EntityManager;
import me.keeleyhoek.bastion.server.objects.Token;
import me.keeleyhoek.bastion.server.objects.Ident;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author escortkeel
 */
public class AuthUtil {

    public static final int ENTROPYBYTES = 32;
    public static final long TOKENEXPIRY = 500000000000L;
    
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final SecureRandom RANDOM = new SecureRandom();

    private AuthUtil() {
    }

    public static Ident buildIdent(String username, String password) {
        String salt = generateSalt();
        return new Ident(username, getPasswordHash(password, salt), salt);
    }

    public static Token authenticate(RequestContext request) throws ParameterException {
        Ident u = request.maybeLookupByField(Ident.class, "user", "username");
        if(u == null) {
            return null;
        }
        
        String pass = request.getParam(String.class, "pass");
        if (u.getPasswordHash().equals(getPasswordHash(pass, u.getSalt()))) {
            return generateToken(request.getEM(), u);
        }
        return null;
    }

    public static String getPasswordHash(String pass, String salt) {
        return doHash(CHARSET.encode(pass), CHARSET.encode(salt));
    }

    private static String generateSalt() {
        byte[] b = new byte[ENTROPYBYTES];
        RANDOM.nextBytes(b);
        return doHash(ByteBuffer.wrap(b));
    }

    private static Token generateToken(EntityManager em, Ident u) {
        Token token = new Token(u, generateSalt(), System.currentTimeMillis() + TOKENEXPIRY);
        em.persist(token);

        return token;
    }

    private static String doHash(ByteBuffer... args) {
        try {
            MessageDigest m = MessageDigest.getInstance("SHA-1");
            for (ByteBuffer b : args) {
                m.update(b);
            }
            return CHARSET.decode(ByteBuffer.wrap(Base64.encodeBase64(m.digest()))).toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Could not obtain a MessageDigest instance", ex);
        }
    }
}
