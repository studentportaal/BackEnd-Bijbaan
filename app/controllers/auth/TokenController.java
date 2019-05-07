package controllers.auth;

import dal.jpa.JPATokenRepository;
import models.authentication.Authenticate;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

import static models.domain.Role.Administrator;

public class TokenController extends Controller {

    private JPATokenRepository tokenRepository;

    @Inject
    public TokenController(JPATokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }


    @Authenticate(requiredRole = Administrator)
    public Result refreshToken() {
        return ok("ok");
    }

}
