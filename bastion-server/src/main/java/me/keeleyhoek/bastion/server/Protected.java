package me.keeleyhoek.bastion.server;

/**
 *
 * @author khoek
 */
public interface Protected {
    public boolean canAccess(RequestContext r) throws ParameterException;
}
