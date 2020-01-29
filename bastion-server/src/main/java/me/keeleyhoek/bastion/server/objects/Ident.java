package me.keeleyhoek.bastion.server.objects;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author khoek
 */
@Entity
@Table(name = "Identity")
public class Ident implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "salt")
    private String salt;
    
    public Ident() {
    }
    
    public Ident(String username, String hash, String salt) {
        this.username = username;
        this.passwordHash = hash;
        this.salt = salt;
    }

    public int getId() {
        return id;
    }        

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }
}
