package me.keeleyhoek.bastion.server;

/**
 *
 * @author escortkeel
 */
public abstract class RequestParser {

    protected abstract Object lookup(RequestContext r, Class clazz) throws ParameterException;
}
