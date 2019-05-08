package dal.jpa;

import dal.context.DatabaseExecutionContext;
import dal.repository.SkillRepository;
import models.domain.Skill;
import org.hibernate.query.Query;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JPASkillRepository implements SkillRepository {

    private final JPAApi jpaApi;

    @Inject
    public JPASkillRepository(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    @Override
    public CompletionStage<Skill> add(Skill skill) {
        return supplyAsync(() -> wrap(em -> add(em, skill)));
    }

    @Override
    public CompletionStage<Skill> update(Skill skill) {
        return supplyAsync(() -> wrap(em -> update(em, skill)));
    }

    @Override
    public void remove(Skill skill) {
        jpaApi.withTransaction(em -> {
            em.remove(skill);
        });
    }

    @Override
    public CompletionStage<List<Skill>> get() {
        return supplyAsync(() -> wrap(em -> get(em)));
    }

    @Override
    public CompletionStage<Skill> get(String id) {
        return supplyAsync(() -> wrap(em -> get(em, id)));
    }

    @Override
    public CompletionStage<List<Skill>> search(String query) {
        return supplyAsync(() -> wrap(em -> search(em, query)));
    }

    private Skill add(EntityManager em, Skill skill) {
        em.persist(skill);
        return skill;
    }

    private Skill update(EntityManager em, Skill skill) {
        em.merge(skill);
        return skill;
    }

    private List<Skill> get(EntityManager em) {
        TypedQuery<Skill> query = em.createNamedQuery("skill.getAll", Skill.class);

        return query.getResultList();
    }

    private Skill get(EntityManager em, String id) {
        TypedQuery<Skill> query = em.createNamedQuery("skill.getById", Skill.class);
        query.setParameter("id", id);

        return query.getSingleResult();
    }

    private List<Skill> search(EntityManager em, String q) {
        TypedQuery<Skill> query = em.createNamedQuery("skill.search", Skill.class);
        query.setParameter("name", "%" + q.toUpperCase() + "%");

        return query.getResultList();
    }
}
