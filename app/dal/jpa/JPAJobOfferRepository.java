package dal.jpa;

import dal.context.DatabaseExecutionContext;
import dal.repository.JobOfferRepository;
import models.domain.Application;
import models.domain.JobOffer;
import models.domain.Skill;
import models.domain.Student;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;


public class JPAJobOfferRepository implements JobOfferRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAJobOfferRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<JobOffer> addJobOffer(JobOffer jobOffer) {
        return supplyAsync(()
                -> wrap(em -> insert(em, jobOffer)), executionContext);
    }

    @Override
    public CompletionStage<JobOffer> removeJobOffer(JobOffer jobOffer) {
        return null;
    }

    @Override
    public CompletionStage<JobOffer> updateJobOffer(JobOffer jobOffer) {
        JobOffer j = wrap(em -> getJobOfferById(em, jobOffer.getId()));
        jobOffer.setApplications(j.getApplications());
        jobOffer.setCompany(j.getCompany());
        return supplyAsync(()
                -> wrap(em -> update(em, jobOffer)), executionContext);
    }

    @Override
    public CompletionStage<JobOffer> getJobOfferById(String id) {
        return supplyAsync(() -> wrap((EntityManager em) -> getJobOfferById(em, id)));
    }

    @Override
    public CompletionStage<List<JobOffer>> getAllJobOffers(int startNr, int amount, String companies, boolean isOpen) {
        return supplyAsync(()
                -> wrap(em -> list(em, startNr, amount, companies, isOpen)), executionContext);
    }

    @Override
    public CompletionStage<List<JobOffer>> getAllJobOffers() {
        return supplyAsync(()
                -> wrap(this::allList), executionContext);
    }

    public JobOffer getJobOfferById(EntityManager em, String id) {
        try {
            TypedQuery<JobOffer> namedQuery = em.createNamedQuery("JobOffer.getJobOfferById", JobOffer.class);
            namedQuery.setParameter("id", id);
            return namedQuery.getSingleResult();
        } catch (EntityNotFoundException | NoResultException e) {
            return null;
        }
    }

    @Override
    public CompletionStage<String> getJobOfferCount(String companies) {
        return supplyAsync(()
                -> wrap(em -> count(em, companies)), executionContext);
    }

    @Override
    public CompletionStage<JobOffer> applyForJob(Application application, String id) {

        JobOffer offer = wrap(em -> getJobOfferById(em, id));
        offer.getApplications().add(application);

        return supplyAsync(()
                -> wrap(em -> update(em, offer)));
    }

    @Override
    public CompletionStage<JobOffer> setSkills(List<Skill> skills, String id) {
        JobOffer offer = wrap(em -> getJobOfferById(em, id));
        offer.setSkills(skills);

        return supplyAsync(()
                -> wrap(em -> update(em, offer)));
    }

    @Override
    public CompletionStage<JobOffer> setTopOfDay(String id, Date topOfDay) {
        JobOffer offer = wrap(em -> getJobOfferById(em, id));
        if (offer.getTopOfTheDay() == null) {
            offer.setTopOfTheDay(new Date());
        }
        offer.setTopOfTheDay(topOfDay);

        return supplyAsync(() -> wrap(em -> update(em, offer)));
    }

    @Override
    public CompletionStage<List<JobOffer>> getAllTopOfDays() {
        return supplyAsync(()
                -> wrap(this::allTopOfDays), executionContext);
    }

    @Override
    public CompletionStage<List<JobOffer>> getForUser(String id) {
        return supplyAsync(() -> wrap(em -> getForUser(em, id)), executionContext);
    }

    private JobOffer insert(EntityManager em, JobOffer jobOffer) {
        em.persist(jobOffer);
        return jobOffer;
    }

    private JobOffer update(EntityManager em, JobOffer offer) {
        em.merge(offer);
        return offer;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private String count(EntityManager em, String companies) {
        Query q;
        if (companies != null && !companies.isEmpty()) {
            List<String> companyList = Arrays.asList(companies.split(","));
            q = em.createQuery("SELECT COUNT (j) FROM JobOffer j WHERE company_uuid IN :companies  AND j.isOpen = true")
            .setParameter("companies",companyList);
        } else{
            q = em.createQuery("SELECT COUNT (j) FROM JobOffer j  WHERE j.isOpen = true");
        }
        return q.getSingleResult().toString();

    }

    private List<JobOffer> list(EntityManager em, int startNr, int amount, String companies, boolean isOpen) {
        TypedQuery<JobOffer> jobOffers;
        if (companies != null && !companies.isEmpty()) {
            List<String> companyList = Arrays.asList(companies.split(","));
            jobOffers = em.createQuery("FROM JobOffer j  WHERE company_uuid IN :companies AND j.isOpen = :open", JobOffer.class);
            jobOffers.setParameter("companies", companyList);
            jobOffers.setParameter("open", isOpen);
        } else {
            jobOffers = em.createQuery("FROM JobOffer j WHERE j.isOpen = :open", JobOffer.class);
            jobOffers.setParameter("open", isOpen);
        }
        jobOffers.setFirstResult(startNr);
        jobOffers.setMaxResults(amount);
        return jobOffers.getResultList();
    }

    private List<JobOffer> allList(EntityManager em) {
        TypedQuery<JobOffer> jobOffers = em.createQuery("FROM JobOffer j", JobOffer.class);
        return jobOffers.getResultList();
    }

    private List<JobOffer> allTopOfDays(EntityManager em) {
        TypedQuery<JobOffer> jobOffers = em.createQuery("FROM JobOffer j WHERE j.topOfTheDay > timestampadd(hour, -24, now()) AND j.topOfTheDay IS NOT NULL AND j.isOpen = true ORDER BY RAND()", JobOffer.class);
        return jobOffers.setMaxResults(3).getResultList();
    }

    private List<JobOffer> getForUser(EntityManager em, String id) {
        TypedQuery<JobOffer> query = em.createNamedQuery("JobOffer.getByApplicant", JobOffer.class)
                .setParameter("id", id);
        return query.getResultList();
    }

}
