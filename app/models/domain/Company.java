package models.domain;

import play.data.validation.Constraints;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name = "Company.getCompanyById", query = "SELECT c FROM Company c WHERE c.uuid = :uuid"),
        @NamedQuery(name = "Company.getAllCompanies", query = "SELECT c FROM Company c ORDER BY c.name ASC"),
})
public class Company extends User implements Comparable<Company> {

    private String name;
    private String city;
    private String streetname;
    private int housenumber;
    private String postalcode;
    private String description;


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

    public String getStreetname() {
        return streetname;
    }

    public void setStreetname(String streetname) {
        this.streetname = streetname;
    }

    public int getHousenumber() {
        return housenumber;
    }

    public void setHousenumber(int housenumber) {
        this.housenumber = housenumber;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", streetname='" + streetname + '\'' +
                ", housenumber=" + housenumber +
                ", postalcode='" + postalcode + '\'' +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }

    @Override
    public int compareTo(Company company) {
        return this.name.compareTo(company.name);
    }
}
