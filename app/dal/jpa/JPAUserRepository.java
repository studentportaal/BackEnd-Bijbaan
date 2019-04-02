package dal.jpa;

import dal.context.DatabaseExecutionContext;
import dal.repository.UserRepository;
import models.domain.User;
import play.db.jpa.JPAApi;
import security.PasswordHelper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Provide JPA operations running inside of a thread pool sized to the connection pool
 */
public class JPAUserRepository implements UserRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAUserRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<User> add(User user) {
        return supplyAsync(() -> wrap(em -> insert(em, user)), executionContext);
    }

    @Override
    public CompletionStage<User> edit(User user) {
        return supplyAsync( () -> wrap(em -> update(em, user)), executionContext);
    }

    @Override
    public CompletionStage<Stream<User>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    @Override
    public CompletionStage<Boolean> login(String email, String password) {
        byte[] salt = getUserHash(email);
        byte[] dBHashedPassword = getPassword(email);
        byte[] userHashedPassword = PasswordHelper.generateHash(salt, password);

        return supplyAsync(() -> Arrays.equals(dBHashedPassword, userHashedPassword)) ;
    }

    @Override
    public CompletionStage<User> getById(String id) {
        return supplyAsync(()->wrap(em -> getById(em,id) ),executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private User insert(EntityManager em, User user) {
        em.persist(user);
        return user;
    }
    private User getById(EntityManager em, String id){
        return em.createNamedQuery("getUser", User.class).setParameter("id",id).getSingleResult();

    }

    private User update(EntityManager em, User user){
        em.merge(user);
        return user;
    }

    private Stream<User> list(EntityManager em) {
        List<User> users = em.createNamedQuery("getUsers", User.class).getResultList();
        return users.stream();
    }

    @Transactional
    byte[] getPassword(String email) {
        return jpaApi.em("em").createNamedQuery("getPassword", byte[].class)
                .setParameter("email", email)
                .getSingleResult();
    }

    @Transactional
    byte[] getUserHash(String email) {
        return jpaApi.em("em").createNamedQuery("getSalt", byte[].class)
                .setParameter("email", email)
                .getSingleResult();
    }
}
