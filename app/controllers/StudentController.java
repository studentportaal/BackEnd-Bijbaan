package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.jpa.JPATokenRepository;
import dal.repository.StudentRepository;
import models.api.ApiError;
import models.authentication.AuthenticationToken;
import models.authentication.JwtEncoder;
import models.converters.StudentConverter;
import models.domain.Role;
import models.domain.Student;
import models.domain.User;
import models.dto.StudentDto;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;


/**
 * The controller keeps all database operations behind the repository, and uses
 * {@link play.libs.concurrent.HttpExecutionContext} to provide access to the
 * {@link play.mvc.Http.Context} methods like {@code request()} and {@code flash()}.
 */
public class StudentController extends Controller {

    private final FormFactory formFactory;
    private final StudentRepository studentRepository;
    private final StudentConverter studentConverter;
    private JPATokenRepository tokenRepository;

    @Inject
    public StudentController(FormFactory formFactory, StudentRepository studentRepository, JPATokenRepository tokenRepository) {
        this.formFactory = formFactory;
        this.studentRepository = studentRepository;
        this.tokenRepository = tokenRepository;
        this.studentConverter = new StudentConverter();
    }

    public Result index(final Http.Request request) {
        return ok("ok");
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addStudent(final Http.Request request) {
        JsonNode json = request.body().asJson();

        Form<StudentDto> userValidationForm = formFactory.form(StudentDto.class)
                .bindFromRequest(request);

        if (userValidationForm.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json object")));
        }

        StudentDto studentDto = Json.fromJson(json, StudentDto.class);

        byte[] salt = PasswordHelper.generateSalt();
        byte[] password = PasswordHelper.generateHash(salt, studentDto.getPassword());

        Student user;
        try {
            user = studentConverter.convertDtoToStudent(studentDto);
        } catch (ParseException e) {
            return badRequest(toJson(new ApiError<>("Invalid date, use: yyyy-MM-dd")));
        }
        user.setSalt(salt);
        user.setPassword(password);
        Set<Role> roles = new HashSet<>(Arrays.asList(Role.User, Role.Student));
        user.setRoles(roles);

        try {
            User addedUser = studentRepository.add(user).toCompletableFuture().get();
            return ok(toJson(addedUser));
        } catch (ConstraintViolationException | InterruptedException | ExecutionException e) {
            return badRequest(toJson(new ApiError<>("A user with this email already exists")));
        }
    }

    public Result getAllStudents() throws ExecutionException, InterruptedException {
        return ok(toJson(studentRepository.list().toCompletableFuture().get().collect(Collectors.toList())));
    }

    @SuppressWarnings("Duplicates")
    public Result updateStudent(Http.Request request, String id){
        JsonNode json = request.body().asJson();

        StudentDto dto = Json.fromJson(json, StudentDto.class);

        StudentConverter converter = new StudentConverter();

        Student user;
        try {
            user = converter.convertDtoToStudent(dto);
        } catch (ParseException e) {
            return badRequest(toJson(new ApiError<>("Invalid date, use: yyyy-MM-dd")));
        }

        studentRepository.edit(user);
        return ok(toJson(user));
    }


    public Result getStudent(String id)  throws InterruptedException, ExecutionException {
        try{
            return ok(toJson(studentRepository.getById(id).toCompletableFuture().get()));
        } catch (NullPointerException e){
            return badRequest(toJson(new ApiError<>("User not found")));
        }
    }

    @SuppressWarnings("Duplicates")
    public Result login(Http.Request request){

        JsonNode json = request.body().asJson();
        StudentDto studentDto = Json.fromJson(json, StudentDto.class);

        if(studentDto.getEmail() == null || studentDto.getPassword() == null || studentDto.getEmail().isEmpty() || studentDto.getPassword().isEmpty()){
            return badRequest(toJson(new ApiError<>("Invalid json format")));
        }

        try {
            Student student = studentRepository.login(studentDto.getEmail(), studentDto.getPassword()).toCompletableFuture().get();

            CompletionStage<AuthenticationToken> token = tokenRepository.createToken(student);

            String jwt = JwtEncoder.toJWT(token.toCompletableFuture().get());

            return ok(toJson(jwt));
        } catch (InterruptedException | ExecutionException | NoResultException e) {
            return badRequest(toJson(new ApiError<>("Invalid username and/or password")));
        }
    }

}
