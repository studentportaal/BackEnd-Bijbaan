package dal.jpa;

import dal.context.DatabaseExecutionContext;
import dal.repository.ApplicationRepository;
import models.domain.Application;
import play.db.jpa.JPAApi;
import scala.App;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * @author Max Meijer
 * Created on 14/05/2019
 */
public class JPAApplicationRepository implements ApplicationRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAApplicationRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Application> add(Application application) {
        return supplyAsync(() -> wrap(em -> add(em, application)), executionContext);
    }

    @Override
    public CompletionStage<Application> update(Application application) {
        return supplyAsync(() -> wrap(em -> update(em, application)), executionContext);
    }

    @Override
    public void remove(Application application) {
        jpaApi.withTransaction(em -> {
            em.remove(application);
        });
    }

    @Override
    public CompletionStage<Application> get(String id) {
        return supplyAsync(() -> wrap(em -> get(em, id)), executionContext);
    }

    @Override
    public CompletionStage<List<Application>> getByCompnay(String id) {
        return supplyAsync(() -> wrap(em -> getByCompanyQuery(em, id)), executionContext);
    }

    @Override
    public void markAccepted(String jobOfferId, String applicationId) {
        jpaApi.withTransaction(em -> {
            markAccepted(em, jobOfferId, applicationId);
        });
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Application add(EntityManager em, Application application) {
        em.persist(application);

        return application;
    }

    private Application update(EntityManager em, Application application) {
        em.merge(application);

        return application;
    }

    private void remove(EntityManager em, Application application) {
        em.remove(application);
    }

    private Application get(EntityManager em, String id) {
        TypedQuery<Application> query = em.createNamedQuery("Application.getById", Application.class);
        query.setParameter("id", id);

        return query.getSingleResult();
    }

    private List<Application> getByCompanyQuery(EntityManager em, String id) {
        TypedQuery<Application> query = em.createNamedQuery("Application.getByCompany", Application.class);
        query.setParameter("id", id);

        return query.getResultList();
    }

    private void markAccepted(EntityManager em, String jobOfferId, String applicationId) {
        Query applicationQuery = em.createNamedQuery("Application.markAccepted");
        applicationQuery.setParameter("id", applicationId);

        Query jobOfferQuery = em.createNamedQuery("JobOffer.markClosed");
        jobOfferQuery.setParameter("id", jobOfferId);

        applicationQuery.executeUpdate();
        jobOfferQuery.executeUpdate();
    }
}
