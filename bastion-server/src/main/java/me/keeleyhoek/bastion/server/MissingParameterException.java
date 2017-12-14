package me.keeleyhoek.bastion.server;

import me.keeleyhoek.bastion.Response;

/**
 *
 * @author escortkeel
 */
public class MissingParameterException extends ParameterException {

    public MissingParameterException(String parameter) {
        super(parameter, Response.Status.MISSING_PARAMETER);
    }
}
