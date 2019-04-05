package dal.repository;

import com.google.inject.ImplementedBy;
import dal.jpa.JPAJobOfferRepository;
import models.domain.JobOffer;
import models.domain.User;

import play.mvc.Result;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(JPAJobOfferRepository.class)
public interface JobOfferRepository {
    CompletionStage<JobOffer> addJobOffer(JobOffer jobOffer);

    CompletionStage<JobOffer> removeJobOffer(JobOffer jobOffer);

    CompletionStage<JobOffer> updateJobOffer(JobOffer jobOffer);

    CompletionStage<JobOffer> getJobOfferById(String id);

    CompletionStage<List<JobOffer>> getAllJobOffers(int startNr, int amount);

    CompletionStage<List<JobOffer>> getAllJobOffers();

    CompletionStage<String> getJobOfferCount();

    CompletionStage<JobOffer> applyForJob(User user, String id);

}
