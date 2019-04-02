package models.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;

@Entity
@NamedQueries({
        @NamedQuery(name = "getUsers", query = "select u from User u"),
        @NamedQuery(name = "getSalt", query = "select u.salt from User u WHERE u.email =:email"),
        @NamedQuery(name = "getPassword", query = "select u.password from User u WHERE u.email =:email"),
        @NamedQuery(name = "getUserByEmail", query = "select u from User u WHERE u.email =:email")
})
public class User {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String uuid;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private Date dateOfBirth;
    private String institute;
    private byte[] salt;
    private byte[] password;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String id) {
        this.uuid = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", salt=" + Arrays.toString(salt) +
                ", password=" + Arrays.toString(password) +
                '}';
    }

}
