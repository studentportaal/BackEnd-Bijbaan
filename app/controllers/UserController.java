package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.jpa.JPATokenRepository;
import dal.repository.UserRepository;
import models.api.ApiError;
import models.authentication.AuthenticationToken;
import models.authentication.JwtEncoder;
import models.converters.UserConverter;
import models.domain.User;
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
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;
import java.text.ParseException;
import java.util.concurrent.CompletionStage;
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
    private JPATokenRepository tokenRepository;
    private final UserConverter userConverter;

    @Inject
    public UserController(FormFactory formFactory, UserRepository userRepository, JPATokenRepository tokenRepository) {
        this.formFactory = formFactory;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.userConverter = new UserConverter();
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

        User user;
        try {
            user = userConverter.convertDtoToUser(userDto);
        } catch (ParseException e) {
            return badRequest(toJson(new ApiError<>("Invalid date, use: yyyy-MM-dd")));
        }
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

    @SuppressWarnings("Duplicates")
    public Result updateUser(Http.Request request, String id){
        JsonNode json = request.body().asJson();

        UserDto dto = Json.fromJson(json, UserDto.class);

        UserConverter converter = new UserConverter();

        User user;
        try {
            user = converter.convertDtoToUser(dto);
        } catch (ParseException e) {
            return badRequest(toJson(new ApiError<>("Invalid date, use: yyyy-MM-dd")));
        }

        userRepository.edit(user);
        return ok(toJson(user));
    }


    public Result getUser(String id)  throws InterruptedException, ExecutionException {
        try{
            return ok(toJson(userRepository.getById(id).toCompletableFuture().get()));
        } catch (NullPointerException e){
            return badRequest(toJson(new ApiError<>("User not found")));
        }
    }

    @SuppressWarnings("Duplicates")
    public Result login(Http.Request request){

        JsonNode json = request.body().asJson();
        UserDto userDto = Json.fromJson(json, UserDto.class);

        if(userDto.getEmail() == null || userDto.getPassword() == null || userDto.getEmail().isEmpty() || userDto.getPassword().isEmpty()){
            return badRequest(toJson(new ApiError<>("Invalid json format")));
        }

        try {
            User user = userRepository.login(userDto.getEmail(), userDto.getPassword()).toCompletableFuture().get();
            CompletionStage<AuthenticationToken> token = tokenRepository.createToken(user);

            String jwt = JwtEncoder.toJWT(token.toCompletableFuture().get());

            return ok(toJson(jwt));
        } catch (InterruptedException | ExecutionException | NoResultException e) {
            return badRequest(toJson(new ApiError<>("Invalid username and/or password")));
        }
    }

}
