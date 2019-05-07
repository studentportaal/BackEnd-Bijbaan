package dal.jpa;

import dal.context.DatabaseExecutionContext;
import models.authentication.AuthenticationToken;
import models.domain.User;
import play.cache.AsyncCacheApi;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JPATokenRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;
    private AsyncCacheApi cache;

    @Inject
    public JPATokenRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext, AsyncCacheApi cache) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
        this.cache = cache;
    }


    public CompletionStage<AuthenticationToken> createToken(User user) {

        return supplyAsync(() -> wrap(em -> insert(em, user)));
    }

    public CompletionStage<Boolean> isTokenValid(String id, String userId, int expirationTime) {
        return supplyAsync(() -> wrap(em -> {
            CompletionStage<AuthenticationToken> futureToken = cache.getOrElseUpdate(id, () -> findOneAsync(em, id));
            try {
                AuthenticationToken token = futureToken.toCompletableFuture().get();

                return (token != null && token.getUser().getUuid().equals(userId) && !token.isExpired(expirationTime));

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return false;
            }

        }), executionContext);
    }

    public CompletionStage<Boolean> isTokenValid(AuthenticationToken token) {
        return isTokenValid(token.getId(), token.getUser().getUuid(), 3600);
    }

    public CompletionStage<AuthenticationToken> getToken(String id) {
        return supplyAsync(() -> wrap(em -> {
            CompletionStage<AuthenticationToken> futureToken = cache.getOrElseUpdate(id, () -> findOneAsync(em, id));
            try {
                AuthenticationToken token = futureToken.toCompletableFuture().get();

                return token;

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }

        }), executionContext);
    }

    public void deleteToken(AuthenticationToken token) {
        jpaApi.withTransaction((Consumer<EntityManager>) entityManager -> entityManager.remove(token));
    }


    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private AuthenticationToken insert(EntityManager em, User user) {
        AuthenticationToken authenticationToken = new AuthenticationToken(user);
        em.persist(authenticationToken);
        return authenticationToken;
    }

    private CompletionStage<AuthenticationToken> findOneAsync(EntityManager em, String id) {
        return supplyAsync(() -> findOne(em, id));
    }

    private AuthenticationToken findOne(EntityManager em, String id) {
        try {
            return em.createNamedQuery("findOne", AuthenticationToken.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (EntityNotFoundException | NoResultException e) {
            return null;
        }
    }
}
