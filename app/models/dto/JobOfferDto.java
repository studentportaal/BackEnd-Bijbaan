package models.dto;

import dal.repository.CompanyRepository;
import dal.repository.StudentRepository;
import models.domain.Company;
import models.domain.JobOffer;
import models.domain.Skill;
import models.domain.Student;
import play.data.validation.Constraints;

import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    private List<ApplicationDto> applications;
    private List<Skill> skills;
    private String company;
    private boolean isOpen;
    private String topOfTheDay;

    public JobOfferDto(JobOffer jobOffer) {
        this.id = jobOffer.getId();
        this.location = jobOffer.getLocation();
        this.title = jobOffer.getTitle();
        this.information = jobOffer.getInformation();
        this.function = jobOffer.getFunction();
        this.salary = jobOffer.getSalary();
        this.isOpen = jobOffer.isOpen();

        if (jobOffer.getApplications() != null && jobOffer.getApplications().size() > 0) {
            this.applications = jobOffer.getApplications().stream().map(ApplicationDto::new).collect(Collectors.toList());
        } else {
            this.applications = new ArrayList<>();
        }

        this.skills = jobOffer.getSkills();
        if( jobOffer.getTopOfTheDay()!= null){
            this.topOfTheDay = jobOffer.getTopOfTheDay().toString();
        }
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

    public List<ApplicationDto> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationDto> applications) {
        this.applications = applications;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public String getTopOfTheDay() {
        return topOfTheDay;
    }

    public void setTopOfTheDay(String topOfTheDay) {
        this.topOfTheDay = topOfTheDay;
    }

    public JobOffer toModel(CompanyRepository repository, StudentRepository studentRepository) {
        JobOffer jobOffer = new JobOffer();
        jobOffer.setInformation(this.getInformation());
        jobOffer.setFunction(this.getFunction());
        jobOffer.setLocation(this.getLocation());
        jobOffer.setSalary(this.getSalary());
        jobOffer.setTitle(this.getTitle());
        jobOffer.setOpen(this.isOpen);

        if (applications != null && applications.size() > 0) {
            jobOffer.setApplications(getApplications().stream().map(a -> a.toModel(studentRepository)).collect(Collectors.toList()));
        } else {
           jobOffer.setApplications(new ArrayList<>());
        }
        jobOffer.setSkills(skills);
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
        if(this.topOfTheDay!= null){
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                jobOffer.setTopOfTheDay(formatter.parse(this.topOfTheDay));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return jobOffer;
    }
}
