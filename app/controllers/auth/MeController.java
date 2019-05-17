package controllers.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.authentication.Authenticate;
import models.authentication.AuthenticateAction;
import models.domain.Role;
import models.domain.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import static play.libs.Json.toJson;

public class MeController extends Controller {

    @Authenticate(requiredRole = Role.USER)
    public Result me(Http.Request request) {
        try {
            User user = request.attrs().get(AuthenticateAction.USER);

            JsonNode userJson = toJson(user);
            ((ObjectNode) userJson).remove("password");
            ((ObjectNode) userJson).remove("salt");

            return ok(userJson);
        } catch (NullPointerException e) {
            return unauthorized("No user for this token");
        }
    }
}
