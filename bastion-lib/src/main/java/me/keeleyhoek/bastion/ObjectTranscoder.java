package me.keeleyhoek.bastion;

import java.lang.reflect.Type;

/**
 *
 * @author khoek
 */
public class ObjectTranscoder {
    
    public static <T> T decodeObject(String str, Type t) {
        return Json.fromJson(str, t);
    }

    public static String encodeObject(Object value) {
        return Json.toJson(value);
    }
}
