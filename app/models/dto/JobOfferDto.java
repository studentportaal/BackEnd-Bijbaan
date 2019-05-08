package models.dto;

import dal.repository.CompanyRepository;
import models.domain.Company;
import models.domain.JobOffer;
import models.domain.Skill;
import models.domain.Student;
import play.data.validation.Constraints;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class JobOfferDto {
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
    private List<Student> applicants;
    private Set<Skill> skills;
    private String company;

    public JobOfferDto(JobOffer jobOffer) {
        this.id = jobOffer.getId();
        this.location = jobOffer.getLocation();
        this.title = jobOffer.getTitle();
        this.information = jobOffer.getInformation();
        this.function = jobOffer.getFunction();
        this.salary = jobOffer.getSalary();
        this.applicants = jobOffer.getApplicants();
        if (jobOffer.getCompany() != null) {
            this.company = jobOffer.getCompany().getUuid();
        }
    }

    public JobOfferDto() {
    }


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

    public List<Student> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<Student> applicants) {
        this.applicants = applicants;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public JobOffer toModel(CompanyRepository repository) {
        JobOffer jobOffer = new JobOffer();
        jobOffer.setInformation(this.getInformation());
        jobOffer.setFunction(this.getFunction());
        jobOffer.setLocation(this.getLocation());
        jobOffer.setSalary(this.getSalary());
        jobOffer.setTitle(this.getTitle());
        jobOffer.setApplicants(this.getApplicants());
        jobOffer.setId(this.getId());
        try {
            if (this.getCompany() != null) {
                Company fullCompany = repository.getCompanyById(this.getCompany()).toCompletableFuture().get();
                if (fullCompany != null) {
                    jobOffer.setCompany(fullCompany);
                    this.setCompany(fullCompany.getUuid());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return jobOffer;
    }
}
