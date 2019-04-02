package dal.jpa;

import dal.context.DatabaseExecutionContext;
import dal.repository.CompanyRepository;
import models.domain.Company;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JPACompanyRepository implements CompanyRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPACompanyRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext){
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Company> add(Company company) {
        return supplyAsync (()
                -> wrap(em -> insert(em, company)), executionContext);
    }

    @Override
    public CompletionStage<Company> update(Company company) {
        return supplyAsync(()
                -> wrap(em -> update(em, company)), executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Company insert(EntityManager em, Company company){
        em.persist(company);
        return company;
    }

    private Company update(EntityManager em, Company company){
        em.merge(company);
        return company;
    }


}
