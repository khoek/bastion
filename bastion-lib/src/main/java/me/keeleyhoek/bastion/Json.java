package me.keeleyhoek.bastion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 *
 * @author khoek
 */
public class Json {

    private static final Gson gson = new GsonBuilder().create();

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T fromJson(Reader r, Type t) {
        return gson.fromJson(r, t);
    }

    public static <T> T fromJson(String str, Type t) {
        return gson.fromJson(str, t);
    }

    private Json() {
    }

}
