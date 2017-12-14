package me.keeleyhoek.bastion.server;

import java.util.List;
import java.util.Set;
import me.keeleyhoek.bastion.Response;
import me.keeleyhoek.bastion.Response.Status;

/**
 *
 * @author escortkeel
 */
public class Responses {
    public static Response error(Status err) {
        return new Response(err, null, null);
    }

    public static Response error(Status err, String msg) {
        return new Response(err, msg, null);
    }

    public static Response success() {
        return new Response(Status.SUCCESS, null, null);
    }

    public static Response success(Set<? extends Packable> c) {
        return new Response(Status.SUCCESS, null, Packables.pack(c));
    }

    public static Response success(List<? extends Packable> c) {
        return new Response(Status.SUCCESS, null, Packables.pack(c));
    }

    public static Response success(Packable p) {
        return new Response(Status.SUCCESS, null, p.pack());
    }

    public static Response success(String s) {
        return new Response(Status.SUCCESS, null, s);
    }

    public static Response success(Enum e) {
        return new Response(Status.SUCCESS, null, e);
    }
    
    private Responses() {
    }
}
