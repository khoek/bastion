package me.keeleyhoek.bastion.server;

import me.keeleyhoek.bastion.Response;

/**
 *
 * @author khoek
 */
public class ParameterException extends Exception {

    private final String parameter;
    private final Response.Status status;
    
    public ParameterException(String parameter, Response.Status status) {
        super("malformed request in parameter: " + parameter + " (" + status + ")");
     
        this.parameter = parameter;
        this.status = status;
    }

    public final String getParameter() {
        return parameter;
    }

    public final Response.Status getStatusCode() {
        return status;
    }
}
