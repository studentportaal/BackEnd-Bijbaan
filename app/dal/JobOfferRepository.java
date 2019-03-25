package dal;

import models.JobOffer;

import java.util.concurrent.CompletionStage;

public interface JobOfferRepository {
    CompletionStage<JobOffer> addJobOffer(JobOffer jobOffer);
    CompletionStage<JobOffer> removeJobOffer(String id);
    CompletionStage<JobOffer> updateJobOffer(JobOffer jobOffer);
    JobOffer getJobOfferById(String id);
}
