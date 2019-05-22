package models.dto;

import models.domain.Skill;
import models.domain.Student;
import play.data.validation.Constraints;

import javax.validation.Constraint;
import java.util.Date;
import java.util.List;

/**
 * @author Max Meijer
 * Created on 26/03/2019
 */
public class StudentDto {
    private String uuid;
    @Constraints.Required
    private String firstName;
    @Constraints.Required
    private String lastName;
    @Constraints.Required
    private String email;
    private String dateOfBirth;
    @Constraints.Required
    private String password;
    @Constraints.Required
    private String institute;
    @Constraints.Required
    private List<Skill> skills;

    public StudentDto() {
        // Empty constructor for Json parsing.
    }

    public StudentDto(String uuid, String firstName, String lastName, String email, String dateOfBirth, String password, String institute) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.institute = institute;
    }

    public StudentDto(String uuid, String email, String firstName, String lastName, Date dateOfBirth, String institute, List<Skill> skills){
        this.uuid = uuid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth.toString();
        this.institute = institute;
        this.skills = skills;
    }

    public StudentDto(Student student) {
        this.uuid = student.getUuid();
        this.email = student.getEmail();
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.dateOfBirth = String.valueOf(student.getDateOfBirth());
        this.institute = student.getInstitute();
        this.skills = student.getSkills();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        return "StudentDto{" +
                "uuid='" + uuid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", password='" + password + '\'' +
                ", institute='" + institute + '\'' +
                '}';
    }
}
