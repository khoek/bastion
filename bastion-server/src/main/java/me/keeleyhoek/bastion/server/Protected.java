package me.keeleyhoek.bastion.server;

/**
 *
 * @author escortkeel
 */
public interface Protected {
    public boolean canAccess(RequestContext r) throws ParameterException;
}
