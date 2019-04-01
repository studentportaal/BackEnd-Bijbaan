package models.dto;

import play.data.validation.Constraints;

/**
 * @author Max Meijer
 * Created on 26/03/2019
 */
public class UserDto {
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

    public UserDto() {
        // Empty constructor for Json parsing.
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

    @Override
    public String toString() {
        return "UserDto{" +
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
