package models.dto;

import dal.repository.StudentRepository;
import models.domain.Application;
import models.domain.Student;
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
    private StudentDto applicant;
    private String motivationLetter;
    private Date applicationDate = new Date();
    private boolean accepted = false;

    public ApplicationDto() {
        // Required no-args constructor
    }

    public ApplicationDto(String id, StudentDto applicant, String motivationLetter, Date applicationDate, boolean accepted) {
        this.id = id;
        this.applicant = applicant;
        this.motivationLetter = motivationLetter;
        this.applicationDate = applicationDate;
        this.accepted = accepted;
    }

    public ApplicationDto(Application application) {
        this.id = application.getId();
        this.applicant = new StudentDto(application.getApplicant());
        this.applicationDate = application.getApplicationDate();
        this.accepted = application.isAccepted();
        this.motivationLetter = application.getMotivationLetter();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StudentDto getApplicant() {
        return applicant;
    }

    public void setApplicant(StudentDto applicant) {
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

        if( application.getApplicant() != null) {
            application.setId(this.id);
        }

        application.setMotivationLetter(this.motivationLetter);
        application.setAccepted(this.accepted);
        try {
            application.setApplicant(repository.getById(this.applicant.getUuid()).toCompletableFuture().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        application.setApplicationDate(applicationDate);

        return application;
    }

    public String getMotivationLetter() {
        return motivationLetter;
    }

    public void setMotivationLetter(String motivationLetter) {
        this.motivationLetter = motivationLetter;
    }
}
