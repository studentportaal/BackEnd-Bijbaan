package models.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author Max Meijer
 * Created on 13/04/2019
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "getStudent", query = "SELECT s FROM Student s WHERE s.uuid = :id" ),
        @NamedQuery(name = "getStudents", query = "SELECT s FROM Student s"),
        @NamedQuery(name = "getSalt", query = "SELECT s.salt FROM Student s WHERE s.email = :email"),
        @NamedQuery(name = "getPassword", query = "SELECT s.password FROM Student s WHERE s.email = :email"),
        @NamedQuery(name = "getStudentByEmail", query = "SELECT s FROM Student s WHERE s.email = :email")
})
public class Student extends User {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String institute;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Skill> skills;

    public Student() {
        // Required no-arg constructor
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    @Override
    public String toString() {
        return "STUDENT{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", institute='" + institute + '\'' +
                "} " + super.toString();
    }
}
