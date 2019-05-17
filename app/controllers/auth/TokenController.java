package controllers.auth;

import dal.jpa.JPATokenRepository;
import models.authentication.Authenticate;
import models.authentication.AuthenticateAction;
import models.authentication.AuthenticationToken;
import models.authentication.JwtEncoder;
import models.domain.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class TokenController extends Controller {

    private JPATokenRepository tokenRepository;

    @Inject
    public TokenController(JPATokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }


    @Transactional
    public Result refreshToken(String refreshKey, String userId) {
        AuthenticationToken token = null;
        try {
            token = tokenRepository.getTokenByRefreshKey(refreshKey).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (token == null || !token.getUser().getUuid().equals(userId))
            return badRequest("Could not refresh token");
        try {
            tokenRepository.deleteToken(token);
            return ok(JwtEncoder.toJWT(tokenRepository.createToken(token.getUser()).toCompletableFuture().get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return internalServerError("Could not create token");
        }
    }

    @Transactional
    @Authenticate
    public Result deleteToken(Http.Request request, String tokenId) {
        User user = request.attrs().get(AuthenticateAction.USER);
        CompletionStage<AuthenticationToken> token = tokenRepository.getToken(tokenId);

        AuthenticationToken authenticationToken = null;
        try {
            authenticationToken = token.toCompletableFuture().get();
            if (authenticationToken.getUser().getUuid().equals(user.getUuid())) {
                tokenRepository.deleteTokensByUser(user.getUuid());

                return ok();
            } else {
                return unauthorized("No matching token found for this user.");
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return internalServerError("oops");
    }

}
