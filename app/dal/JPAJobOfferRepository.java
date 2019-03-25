package dal;

import models.JobOffer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.concurrent.CompletionStage;
import java.util.*;


//TODO:
//  return the right object
public class JPAJobOfferRepository implements JobOfferRepository {
    @PersistenceContext
    EntityManager em;

    @Override
    public CompletionStage<JobOffer> addJobOffer(JobOffer jobOffer) {
        em.persist(jobOffer);
        return null;
    }

    @Override
    public CompletionStage<JobOffer> removeJobOffer(String id) {
        em.remove(null);
        return null;
    }

    @Override
    public CompletionStage<JobOffer> updateJobOffer(JobOffer jobOffer) {
        em.merge(jobOffer);
        return null;
    }

    @Override
    public JobOffer getJobOfferById(String id) {
        TypedQuery query = em.createNamedQuery("JobOffer.getJobOfferById", JobOffer.class);
        query.setParameter("id", id);
        List<JobOffer> jobOfferList = query.getResultList();
        return jobOfferList.get(0);
    }
}
