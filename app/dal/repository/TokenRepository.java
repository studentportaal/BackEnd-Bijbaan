package dal.repository;

import com.google.inject.ImplementedBy;
import dal.jpa.JPATokenRepository;
import models.authentication.AuthenticationToken;
import models.domain.User;

import java.util.concurrent.CompletionStage;

@ImplementedBy(JPATokenRepository.class)
public interface TokenRepository {
    CompletionStage<AuthenticationToken> createToken(User user);

    CompletionStage<Boolean> isTokenValid(String id, String userId, int expirationTime);

    CompletionStage<Boolean> isTokenValid(AuthenticationToken token);

    CompletionStage<AuthenticationToken> getToken(String id);

    CompletionStage<AuthenticationToken> getTokenByRefreshKey(String refreshKey);

    void deleteToken(AuthenticationToken token);

    void deleteTokensByUser(String userId);
}
