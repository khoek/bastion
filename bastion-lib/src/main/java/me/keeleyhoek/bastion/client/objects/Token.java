package me.keeleyhoek.bastion.client.objects;

/**
 *
 * @author khoek
 */
public class Token {

    private final String raw;
    private final long expiry;
    
    public Token(String raw, long expiry) {
        this.raw = raw;
        this.expiry = expiry;
    }

    public String getRaw() {
        return raw;
    }

    public long getExpiry() {
        return expiry;
    }
}
