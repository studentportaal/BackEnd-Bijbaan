package dal.jpa;

import dal.context.DatabaseExecutionContext;
import dal.repository.CompanyRepository;
import models.domain.Company;
import play.db.jpa.JPAApi;
import security.PasswordHelper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JPACompanyRepository implements CompanyRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPACompanyRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Company> add(Company company) {
        return supplyAsync(()
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

    private Company insert(EntityManager em, Company company) {
        em.persist(company);
        return company;
    }

    private Company update(EntityManager em, Company company) {
        em.merge(company);
        return company;
    }

    @Override
    public CompletionStage<Company> getCompanyById(String id) {
        return supplyAsync(() -> wrap((EntityManager em) -> {
            try {
                TypedQuery<Company> namedQuery = em.createNamedQuery("COMPANY.getCompanyById", Company.class);
                namedQuery.setParameter("uuid", id);
                return namedQuery.getSingleResult();
            } catch (EntityNotFoundException | NoResultException e) {
                return null;
            }
        }));
    }

    @Override
    public CompletionStage<Company> login(String email, String password) {
        byte[] salt = wrap(em -> getCompanySalt(em, email));

        return supplyAsync(() -> wrap(em -> getCompanyAndPassword(em, email, PasswordHelper.generateHash(salt, password))));
    }

    @Override
    public CompletionStage<List<Company>> getAllCompanies() {
        return supplyAsync(()
        -> wrap(em -> list(em)), executionContext);
    }

    private Company getCompanyAndPassword(EntityManager em, String email, byte[] hashedPassword){
        TypedQuery<Company> query = em.createQuery(
                "SELECT c " +
                        "FROM Company c WHERE c.email = :email AND c.password = :password", Company.class)
                .setParameter("email", email)
                .setParameter("password", hashedPassword);
        return query.getSingleResult();
    }

    @Transactional
    byte[] getCompanySalt(EntityManager em, String email) {
        TypedQuery<byte[]> query = em.createQuery("SELECT c.salt FROM Company c WHERE c.email = :email", byte[].class);
        query.setParameter("email", email);
        return query.getSingleResult();
    }

    public List<Company> list(EntityManager em){
        TypedQuery<Company> query = em.createNamedQuery("COMPANY.getAllCompanies", Company.class);
        List<Company> allCompanies= query.getResultList();
        return allCompanies;
    }
}
