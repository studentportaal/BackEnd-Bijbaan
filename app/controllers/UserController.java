package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.repository.UserRepository;
import models.domain.User;
import models.api.ApiError;
import models.dto.UserDto;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import security.PasswordHelper;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public Result addUser(final Http.Request request) {
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

        Date parsed;
        try {
            SimpleDateFormat format =
                    new SimpleDateFormat("yyyy-MM-dd");
            parsed = format.parse(userDto.getDateOfBirth());
        }
        catch(ParseException pe) {
            return badRequest(toJson(new ApiError<>("Invalid date, use: yyyy-MM-dd")));
        }

        user.setDateOfBirth(parsed);
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

    public Result getAllUsers() throws ExecutionException, InterruptedException {
        return ok(toJson(userRepository.list().toCompletableFuture().get().collect(Collectors.toList())));
    }

    public Result getUser(String id)  throws InterruptedException, ExecutionException {
        try{
            return ok(toJson(userRepository.getById(id).toCompletableFuture().get()));
        } catch (NullPointerException e){
            return badRequest(toJson(new ApiError<>("User not found")));
        }
    }

}
