package models.dto;

import dal.repository.StudentRepository;
import models.domain.Application;
import play.data.validation.Constraints;

import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * @author Max Meijer
 * Created on 14/05/2019
 */
public class ApplicationDto {
    private String id;
    @Constraints.Required
    private String applicant;
    private Date applicationDate = new Date();
    private boolean accepted = false;

    public ApplicationDto() {
        // Required no-args constructor
    }

    public ApplicationDto(String id, String applicant, Date applicationDate, boolean accepted) {
        this.id = id;
        this.applicant = applicant;
        this.applicationDate = applicationDate;
        this.accepted = accepted;
    }

    public ApplicationDto(Application application) {
        this.id = application.getId();
        this.applicant = application.getApplicant().getUuid();
        this.applicationDate = application.getApplicationDate();
        this.accepted = application.isAccepted();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
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

    public Application toModel(StudentRepository repository)  {
        Application application = new Application();

        application.setId(this.id);
        application.setAccepted(this.accepted);
        try {
            application.setApplicant(repository.getById(this.applicant).toCompletableFuture().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        application.setApplicationDate(applicationDate);

        return application;
    }

}
