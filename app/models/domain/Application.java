package models.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Max Meijer
 * Created on 14/05/2019
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Application.getById", query = "FROM Application WHERE id = :id"),
        @NamedQuery(name = "Application.getByCompany", query = "SELECT applications.id, applications.accepted, applications.applicationDate, applications.applicant FROM JobOffer j JOIN Application AS applications WHERE j.id = :id"),
        @NamedQuery(name = "Application.markAccepted", query = "UPDATE Application a SET a.accepted = true WHERE a.id = :id")
})
public class Application {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @ManyToOne
    private Student applicant;
    private Date applicationDate = new Date();
    private boolean accepted = false;

    public Application() {
        // Required no-arg constructor
    }

    public Application(Student applicant, Date applicationDate, boolean accepted) {
        this.applicant = applicant;
        this.applicationDate = applicationDate;
        this.accepted = accepted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Student getApplicant() {
        return applicant;
    }

    public void setApplicant(Student applicant) {
        this.applicant = applicant;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

}
