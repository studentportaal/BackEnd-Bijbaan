package dal.repository;

import com.google.inject.ImplementedBy;
import dal.jpa.JPAJobOfferRepository;
import models.domain.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(JPAJobOfferRepository.class)
public interface JobOfferRepository {
    CompletionStage<JobOffer> addJobOffer(JobOffer jobOffer);

    CompletionStage<JobOffer> removeJobOffer(JobOffer jobOffer);

    CompletionStage<JobOffer> updateJobOffer(JobOffer jobOffer);

    CompletionStage<JobOffer> getJobOfferById(String id);

    CompletionStage<List<JobOffer>> getAllJobOffers(int startNr, int amount, String companies, boolean isOpen, String skills,String title);

    CompletionStage<List<JobOffer>> getAllJobOffers();

    CompletionStage<Long> getJobOfferCount(String companies, boolean isOpen, String skills,String title);

    CompletionStage<JobOffer> applyForJob(Application application, String id);

    CompletionStage<JobOffer> setSkills(List<Skill> skills, String id);

    CompletionStage<JobOffer> setTopOfDay(String id, Date topOfDay);

    CompletionStage<List<JobOffer>> getAllTopOfDays();

    CompletionStage<List<JobOffer>> getForUser(String id);

}
