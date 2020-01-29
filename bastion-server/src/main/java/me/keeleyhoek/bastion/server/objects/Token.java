package me.keeleyhoek.bastion.server.objects;

import java.io.Serializable;
import javax.persistence.*;
import me.keeleyhoek.bastion.server.Packable;

/**
 *
 * @author khoek
 */
@Entity
@Table(name = "Token")
public class Token implements Serializable, Packable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "raw")
    private String raw;

    @ManyToOne
    @JoinColumn(name = "ident")
    private Ident ident;
    
    @Column(name = "expiry")
    private long expiry;

    public Token() {
    }

    public Token(Ident identity, String raw, long expiry) {
        this.raw = raw;
        this.ident = identity;
        this.expiry = expiry;
    }

    public String getRaw() {
        return raw;
    }

    public Ident getIdent() {
        return ident;
    }
    
    public long getExpiry() {
        return expiry;
    }

    @Override
    public Object pack() {
        return new me.keeleyhoek.bastion.client.objects.Token(raw, expiry);
    }
}
