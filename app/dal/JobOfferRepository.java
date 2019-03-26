package dal;

import com.google.inject.ImplementedBy;
import models.JobOffer;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(JPAJobOfferRepository.class)
public interface JobOfferRepository {
    CompletionStage<JobOffer> addJobOffer(JobOffer jobOffer);
    CompletionStage<JobOffer> removeJobOffer(String id);
    CompletionStage<JobOffer> updateJobOffer(JobOffer jobOffer);
    JobOffer getJobOfferById(String id);
    CompletionStage<Stream<JobOffer>> getAllJobOffers();
}
