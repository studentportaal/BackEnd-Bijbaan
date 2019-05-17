package models.authentication;

import dal.jpa.JPATokenRepository;
import io.jsonwebtoken.Jwts;
import models.domain.User;
import play.libs.typedmap.TypedKey;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class AuthenticateAction extends Action<Authenticate> {

    public static final TypedKey<User> USER = TypedKey.create("user");
    private JPATokenRepository tokenRepository;

    @Inject
    public AuthenticateAction(JPATokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public CompletionStage<Result> call(Http.Request request) {
        System.out.println(request.getHeaders());

        if (!request.hasHeader("authentication")) {
            return supplyAsync(() -> badRequest("Missing Authentication Header"));
        }

        if (!Jwts.parser().isSigned(request.header("authentication").get())) {
            return supplyAsync(() -> unauthorized("Unsigned JWT"));
        }

        AuthenticationToken token = JwtEncoder.fromJWT(request.header("authentication").get(), tokenRepository);

        if (token == null) return supplyAsync(() -> unauthorized("Bad Token"));

        try {
            if (tokenRepository.isTokenValid(token).toCompletableFuture().get()) {
                if (token.getUser().getRoles().contains(configuration.requiredRole())) {
                    request = request.addAttr(USER, token.getUser());
                    return delegate.call(request);
                } else {
                    return supplyAsync(() -> unauthorized("Missing Required Role"));
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return supplyAsync(() -> unauthorized("Insufficient Access"));
    }
}
