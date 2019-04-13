package dal.jpa;

import dal.context.DatabaseExecutionContext;
import dal.repository.UserRepository;
import models.domain.User;
import models.dto.UserDto;
import play.db.jpa.JPAApi;
import security.PasswordHelper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
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
    public CompletionStage<User> login(String email, String password) {
        byte[] salt = wrap(em -> getUserSalt(em, email));

        return supplyAsync(() -> wrap(em -> getUserAndPassword(em, email, PasswordHelper.generateHash(salt, password))));
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
        return em.createNamedQuery("getUser", User.class)
                .setParameter("id",id)
                .getSingleResult();
    }

    private User update(EntityManager em, User user){
        User u = getById(em, user.getUuid());
        user.setSalt(u.getSalt());
        user.setPassword(u.getPassword());
        em.merge(user);
        return user;
    }

    private Stream<User> list(EntityManager em) {
        List<User> users = em.createNamedQuery("getUsers", User.class).getResultList();
        return users.stream();
    }

    private User getUserAndPassword(EntityManager em, String email, byte[] hashedPassword){
        TypedQuery<User> query = em.createQuery(
                "SELECT u " +
                        "FROM User u WHERE u.email = :email AND u.password = :password", User.class)
                .setParameter("email", email)
                .setParameter("password", hashedPassword);
        return query.getSingleResult();
    }
    @Transactional
    byte[] getUserSalt(EntityManager em, String email) {
        TypedQuery<byte[]> query = em.createQuery("SELECT u.salt FROM User u WHERE u.email = :email", byte[].class);
        query.setParameter("email", email);
        return query.getSingleResult();
    }
}
