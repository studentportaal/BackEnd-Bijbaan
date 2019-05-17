package models.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Entity
public abstract class User {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String uuid;
    @Column(unique = true)
    private String email;
    private byte[] salt;
    private byte[] password;
    @ElementCollection(targetClass = Role.class)
    @Enumerated(EnumType.STRING)
    private Collection<Role> roles = Arrays.asList(Role.User);

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String id) {
        this.uuid = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] hash) {
        this.salt = hash;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", email='" + email + '\'' +
                ", salt=" + Arrays.toString(salt) +
                ", password=" + Arrays.toString(password) +
                '}';
    }

    public Collection<Role> getRoles() {
        return roles;
    }
}
