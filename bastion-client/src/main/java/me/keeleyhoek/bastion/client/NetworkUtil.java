package me.keeleyhoek.bastion.client;

import me.keeleyhoek.bastion.client.protocol.HttpProtocolDelegate;

/**
 *
 * @author escortkeel
 */
public class NetworkUtil {

    private static Remote REMOTE;

    public static void init(String url) {
        if(REMOTE != null) {
            throw new IllegalStateException("NetworkUtil already initialised");
        }
        
        REMOTE = new Remote(new HttpProtocolDelegate(url));
    }
    
    public static Remote getRemote() {
        if(REMOTE == null) {
            throw new IllegalStateException("NetworkUtil not yet initialised");
        }
        
        return REMOTE;
    }
}
