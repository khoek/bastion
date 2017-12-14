package me.keeleyhoek.bastion.server;

import me.keeleyhoek.bastion.Response;

/**
 *
 * @author escortkeel
 */
public class IllegalParameterException extends ParameterException {

    public IllegalParameterException(String parameter) {
        super(parameter, Response.Status.BAD_PARAMETER);
    }
}
