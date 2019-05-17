package models.authentication;

import models.domain.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@NamedQueries({
        @NamedQuery(name = "findOne", query = "select a from AuthenticationToken a WHERE a.id = :id"),
        @NamedQuery(name = "findOneWithUserId", query = "select a from AuthenticationToken as a WHERE a.id = :id AND a.user.id = :userId "),
        @NamedQuery(name = "findByRefreshToken", query = "select a from AuthenticationToken as a WHERE  a.refreshKey = :refreshKey"),
        @NamedQuery(name = "deleteByUser", query = "DELETE from AuthenticationToken as a WHERE  a.user.id = :userId"),


})
public class AuthenticationToken {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @ManyToOne
    private User user;
    /**
     * The starting date of the validity of this authentication Token, with which the added duration decides the expiry.
     */
    @CreationTimestamp
    private Date start;
    private String refreshKey;


    public AuthenticationToken(User user) {
        this.user = user;
        refreshKey = UUID.randomUUID().toString();
    }

    public AuthenticationToken() {
    }


    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Date getStart() {
        return start;
    }

    public boolean isExpired(int expirationTime) {
        return start.getTime() + expirationTime < new Date().getTime();
    }

    public String getRefreshKey() {
        return refreshKey;
    }

    public void setStart(Date start) {
        this.start = start;
    }
}