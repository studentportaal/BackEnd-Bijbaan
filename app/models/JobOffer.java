package models;

import org.hibernate.annotations.GenericGenerator;
import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name = "JobOffer.getJobOfferById", query = "SELECT j FROM JobOffer j WHERE j.id = :id"),
        @NamedQuery(name = "JobOffer.getAllJobOffers", query = "SELECT j from JobOffer j")
})

public class JobOffer {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    @Constraints.Required
    private String location;
    @Constraints.Required
    private String title;
    @Constraints.Required
    private String information;
    @Constraints.Required
    private String function;
    @Constraints.Required
    private double salary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
