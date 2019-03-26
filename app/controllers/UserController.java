package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.repository.UserRepository;
import models.domain.User;
import models.api.ApiError;
import models.dto.UserDto;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import security.PasswordHelper;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;


/**
 * The controller keeps all database operations behind the repository, and uses
 * {@link play.libs.concurrent.HttpExecutionContext} to provide access to the
 * {@link play.mvc.Http.Context} methods like {@code request()} and {@code flash()}.
 */
public class UserController extends Controller {

    private final FormFactory formFactory;
    private final UserRepository userRepository;

    @Inject
    public UserController(FormFactory formFactory, UserRepository userRepository) {
        this.formFactory = formFactory;
        this.userRepository = userRepository;
    }

    public Result index(final Http.Request request) {
        return ok("ok");
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addPerson(final Http.Request request) {
        JsonNode json = request.body().asJson();

        Form<UserDto> userValidationForm = formFactory.form(UserDto.class)
                .bindFromRequest(request);

        if (userValidationForm.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json object")));
        }

        UserDto userDto = Json.fromJson(json, UserDto.class);

        byte[] salt = PasswordHelper.generateSalt();
        byte[] password = PasswordHelper.generateHash(salt, userDto.getPassword());

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setEmail(userDto.getEmail());
        user.setInstitute(userDto.getInstitute());
        user.setSalt(salt);
        user.setPassword(password);

        try {
            User addedUser = userRepository.add(user).toCompletableFuture().get();
            return ok(toJson(addedUser));
        } catch (ConstraintViolationException | InterruptedException | ExecutionException e) {
            return badRequest(toJson(new ApiError<>("A user with this email already exists")));
        }
    }

    public Result getPersons() throws ExecutionException, InterruptedException {
        return ok(toJson(userRepository.list().toCompletableFuture().get().collect(Collectors.toList())));
    }

}
