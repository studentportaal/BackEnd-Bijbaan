package models.dto;

import play.data.validation.Constraints;

/**
 * @author Max Meijer
 * Created on 13/04/2019
 */
public class CompanyDto {
    private String uuid;
    @Constraints.Required
    private String email;
    @Constraints.Required
    private String password;
    @Constraints.Required
    private String name;
    @Constraints.Required
    private String city;
    @Constraints.Required
    private String streetName;
    @Constraints.Required
    private String houseNumber;
    @Constraints.Required
    private String postalCode;
    @Constraints.Required
    private String description;

    public CompanyDto() {
        // Required no-arg constructor
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
