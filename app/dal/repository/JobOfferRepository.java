package dal.repository;

import com.google.inject.ImplementedBy;
import dal.jpa.JPAJobOfferRepository;
import models.domain.JobOffer;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(JPAJobOfferRepository.class)
public interface JobOfferRepository {
    CompletionStage<JobOffer> addJobOffer(JobOffer jobOffer);
    CompletionStage<JobOffer> removeJobOffer(JobOffer jobOffer);
    CompletionStage<JobOffer> updateJobOffer(JobOffer jobOffer);
    JobOffer getJobOfferById(String id);
    CompletionStage<List<JobOffer>> getAllJobOffers(int startNr, int amount);
    CompletionStage<List<JobOffer>> getAllJobOffers();
    CompletionStage<String>getJobOfferCount();
}
