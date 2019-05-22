package models.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "JobOffer.getJobOfferById", query = "SELECT j FROM JobOffer j WHERE j.id = :id"),
        @NamedQuery(name = "JobOffer.getAllJobOffers", query = "SELECT j from JobOffer j"),
        @NamedQuery(name = "JobOffer.markClosed", query = "UPDATE JobOffer j SET j.isOpen = false WHERE id = :id")
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
    @Column(columnDefinition = "TEXT")
    private String information;
    @Constraints.Required
    private String function;
    @Constraints.Required
    private double salary;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Application> applications;
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Skill> skills;
    @ManyToOne
    private Company company;
    private boolean isOpen = true;

    private Date topOfTheDay;

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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public boolean isisOpen() {
        return isOpen;
    }

    public void setisOpen(boolean isWat) {
        isOpen = isWat;
    }

    public Date getTopOfTheDay() {
        return topOfTheDay;
    }

    public void setTopOfTheDay(Date topOfTheDay) {
        this.topOfTheDay = topOfTheDay;
    }
}
