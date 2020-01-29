package me.keeleyhoek.bastion.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import me.keeleyhoek.bastion.Response;
import me.keeleyhoek.bastion.client.ProtocolDelegate.Parameter;
import me.keeleyhoek.bastion.client.objects.Token;

/**
 *
 * @author khoek
 */
public class Remote {

    private final ProtocolDelegate delegate;
    private Token token = null;

    public Remote(ProtocolDelegate delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    public synchronized Response<Token> login(String user, String pass) throws IOException {
        Response<Token> r = request("login", Token.class,
                new Parameter("user", user),
                new Parameter("pass", pass)
        );
        
        if (r.getStatus() == Response.Status.SUCCESS) {
            token = r.getObject();
        }
        
        return r;
    }

    public synchronized void logoff(String user, String pass) throws IOException {
        request("logoff");
        
        token = null;
    }

    public synchronized Response request(String path, Parameter... params) throws IOException {
        return request(path, null,  params);
    }

    public synchronized <T> Response<T> request(String path, Class<T> clazz, Parameter... params) throws IOException {
        return request(path, (Type) clazz, params);
    }

    public synchronized <T> Response<T> request(String path, Type t, Parameter... params) throws IOException {
        return delegate.doRequest(token, path, t, params);
    }
}
