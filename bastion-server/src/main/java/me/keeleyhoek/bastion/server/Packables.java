/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.keeleyhoek.bastion.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author escortkeel
 */
public class Packables {

    public static List<? extends Object> pack(List<? extends Packable> l) {
        List<Object> n = new ArrayList<>();
        for(Packable p : l) {
            n.add(p.pack());
        }
        return n;
    }

    public static Set<? extends Object> pack(Set<? extends Packable> s) {
        Set<Object> n = new HashSet<>();
        for(Packable p : s) {
            n.add(p.pack());
        }
        return n;
    }

    private Packables() {
    }
}
