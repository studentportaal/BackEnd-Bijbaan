package dal;

import dal.interfaces.JobOfferRepository;
import models.JobOffer;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

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
        return supplyAsync(new Supplier<JobOffer>() {
            @Override
            public JobOffer get() {
                return JPAJobOfferRepository.this.wrap((EntityManager em) ->
                        JPAJobOfferRepository.this.insert(em, jobOffer));
            }
        }, executionContext);
    }

    @Override
    public CompletionStage<JobOffer> removeJobOffer(JobOffer jobOffer) {
        return null;
    }

    @Override
    public CompletionStage<JobOffer> updateJobOffer(JobOffer jobOffer) {
        return supplyAsync(new Supplier<JobOffer>() {
            @Override
            public JobOffer get() {
                return JPAJobOfferRepository.this.wrap((EntityManager em) ->
                JPAJobOfferRepository.this.update(em, jobOffer));
            }
        }, executionContext);
    }

    @Override
    public JobOffer getJobOfferById(String id) {
        return null;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private JobOffer insert(EntityManager em, JobOffer jobOffer) {
        em.persist(jobOffer);
        return jobOffer;
    }

    private void delete(EntityManager em, JobOffer jobOffer) {
        em.remove(jobOffer);
    }

    private JobOffer update(EntityManager em, JobOffer jobOffer) {
        em.merge(jobOffer);
        return jobOffer;
    }
}
