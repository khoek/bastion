package me.keeleyhoek.bastion.server;

import me.keeleyhoek.bastion.Response;

/**
 *
 * @author khoek
 */
public class AccessDisallowedException extends ParameterException {

    public AccessDisallowedException() {
        super("sid", Response.Status.ACCESS_DISALLOWED);
    }
}
