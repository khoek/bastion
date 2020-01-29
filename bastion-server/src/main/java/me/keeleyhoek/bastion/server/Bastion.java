package me.keeleyhoek.bastion.server;

import javax.persistence.Persistence;

/**
 *
 * @author khoek
 */
public class Bastion {

    public static void init(String persistenceUnit, RequestParser parser) {
        DBManager.EM = Persistence.createEntityManagerFactory(persistenceUnit);
        RequestContext.PARSER = parser;
    }

    private Bastion() {
    }
}
