package me.keeleyhoek.bastion.server;

/**
 *
 * @author khoek
 */
public abstract class RequestParser {

    protected abstract Object lookup(RequestContext r, Class clazz) throws ParameterException;
}
