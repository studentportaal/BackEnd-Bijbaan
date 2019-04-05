package dal.jpa;

import dal.context.DatabaseExecutionContext;
import dal.repository.JobOfferRepository;
import models.domain.JobOffer;
import models.domain.User;
import play.db.jpa.JPAApi;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;


public class JPAJobOfferRepository implements JobOfferRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAJobOfferRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext){
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<JobOffer> addJobOffer(JobOffer jobOffer) {
        return supplyAsync(()
                -> JPAJobOfferRepository.this.wrap((EntityManager em)
                -> JPAJobOfferRepository.this.insert(em, jobOffer)), executionContext);
    }

    @Override
    public CompletionStage<JobOffer> removeJobOffer(JobOffer jobOffer) {
        return null;
    }

    @Override
    public CompletionStage<JobOffer> updateJobOffer(JobOffer jobOffer) {
        return null;
    }

    @Override
    public CompletionStage<JobOffer> getJobOfferById(String id) {
        return supplyAsync(() -> wrap((EntityManager em) -> {
            TypedQuery<JobOffer> namedQuery = em.createNamedQuery("JobOffer.getJobOfferById", JobOffer.class);
            namedQuery.setParameter("id", id);
            return namedQuery.getSingleResult();
        }));
    }

    @Override
    public CompletionStage<List<JobOffer>> getAllJobOffers(int startNr, int amount) {
        return supplyAsync(() -> wrap(em -> list(em, startNr, amount)), executionContext);
    }

    @Override
    public CompletionStage<List<JobOffer>> getAllJobOffers() {
        return supplyAsync(() -> wrap(this::allList), executionContext);
    }

    @Override
    public CompletionStage<String> getJobOfferCount() {
        return supplyAsync(() -> wrap(this::count), executionContext);
    }

    @Override
    public CompletionStage<Result> applyForJob(User user, String id) {
        return null;
    }

    private JobOffer insert(EntityManager em, JobOffer jobOffer) {
        em.persist(jobOffer);
        return jobOffer;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private String count(EntityManager em) {
        Query q = em.createQuery("SELECT COUNT (j) FROM JobOffer j");
        return q.getSingleResult().toString();

    }

    private List<JobOffer> list(EntityManager em, int startNr, int amount) {
        TypedQuery<JobOffer> jobOffers = em.createQuery("FROM JobOffer j", JobOffer.class);
        jobOffers.setFirstResult(startNr);
        jobOffers.setMaxResults(amount);
        return jobOffers.getResultList();
    }

    private List<JobOffer> allList(EntityManager em) {
        TypedQuery<JobOffer> jobOffers = em.createQuery("FROM JobOffer j", JobOffer.class);
        return jobOffers.getResultList();
    }
}
