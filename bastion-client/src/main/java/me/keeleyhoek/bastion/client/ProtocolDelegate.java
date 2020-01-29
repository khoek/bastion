package me.keeleyhoek.bastion.client;
import java.io.IOException;
import java.lang.reflect.Type;
import me.keeleyhoek.bastion.Response;
import me.keeleyhoek.bastion.client.objects.Token;

/**
 *
 * @author khoek
 */
public abstract class ProtocolDelegate {

    public static class Parameter {

        private final String key;
        private final Object value;

        public Parameter(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }
    }

    protected abstract <T> Response<T> doRequest(Token token, String path, Type t, Parameter... params) throws IOException;
}
