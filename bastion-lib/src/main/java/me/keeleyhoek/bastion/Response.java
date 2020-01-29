package me.keeleyhoek.bastion;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * @author khoek
 * @param <T>
 */
public class Response<T> {

    private static class ParameterizedResponseType implements ParameterizedType {

        private final Type type;

        public ParameterizedResponseType(Type type) {
            this.type = type;
        }

        @Override
        public Type getRawType() {
            return Response.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{type};
        }
    }

    public static void send(Writer w, Response r) {
        try {
            w.write(ObjectTranscoder.encodeObject(r));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Response read(Reader r) {
        return read(r, null);
    }

    public static Response read(String str) {
        return read(str, null);
    }

    public static <T> Response<T> read(Reader r, Type t) {
        if (t == null) {
            return Json.fromJson(r, Response.class);
        } else {
            return Json.fromJson(r, new ParameterizedResponseType(t));
        }
    }

    public static <T> Response<T> read(String str, Type t) {
        if (t == null) {
            return Json.fromJson(str, Response.class);
        } else {
            return Json.fromJson(str, new ParameterizedResponseType(t));
        }
    }

    private final Status status;
    private final String message;
    private final T object;

    public Response(Status status, String message, T object) {
        this.status = status;
        this.message = message;
        this.object = object;

        assert ((status == Status.SUCCESS) == (message == null));
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getObject() {
        return object;
    }

    public static enum Status {

        CLIENT_EXCEPTION,
        SUCCESS,
        ACCESS_DISALLOWED,
        MISSING_PARAMETER,
        BAD_PARAMETER,
        BAD_TOKEN,
        BAD_CREDENTIALS,;

        private Status() {
        }
    }
}
