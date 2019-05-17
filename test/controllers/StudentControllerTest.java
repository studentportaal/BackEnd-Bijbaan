package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dal.repository.StudentRepository;
import dal.repository.TokenRepository;
import models.api.ApiError;
import models.domain.Skill;
import models.domain.Student;
import models.dto.StudentDto;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.Test;
import play.data.FormFactory;
import play.data.format.Formatters;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.test.Helpers.contentAsString;

/**
 * @author Max Meijer
 * Created on 27/03/2019
 */
public class StudentControllerTest {

    @Test
    public void checkAddUser() {
        // Don't need to be this involved in setting up the mock, but for demo it works:
        StudentRepository repository = mock(StudentRepository.class);
        TokenRepository tokenRepository = mock(TokenRepository.class);

        Skill skill =new Skill("Java");

        StudentDto dto = new StudentDto();
        dto.setFirstName("Steve");
        dto.setLastName("Smith");
        dto.setDateOfBirth("1990-01-01");
        dto.setEmail("test@test.nl");
        dto.setInstitute("Fontys");
        dto.setPassword("password");
        dto.setSkills(Arrays.asList(skill));

        Student student = new Student();
        student.setFirstName("Steve");

        when(repository.add(any())).thenReturn(supplyAsync(() -> student));

        // Set up the request builder to reflect input
        Http.Request request = Helpers.fakeRequest("POST", "/").bodyJson(Json.toJson(dto)).build().withTransientLang("es");

        // Easier to mock out the form factory inputs here
        Messages messages = mock(Messages.class);
        MessagesApi messagesApi = mock(MessagesApi.class);
        when(messagesApi.preferred(request)).thenReturn(messages);

        ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();

        Config config = ConfigFactory.load();
        FormFactory formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validatorFactory, config);

        // Create controller and call method under test:
        final StudentController controller = new StudentController(formFactory, repository, tokenRepository);

        Result stage = controller.addStudent(request);
        String result = contentAsString(stage);

        Student studentResult = Json.fromJson(Json.parse(result), Student.class);

        assertEquals(200, stage.status());
        assertEquals(student.getFirstName(), studentResult.getFirstName());
    }

    @Test
    public void checkAddUserInvalidObject() {
        // Don't need to be this involved in setting up the mock, but for demo it works:
        StudentRepository repository = mock(StudentRepository.class);
        TokenRepository tokenRepository = mock(TokenRepository.class);

        StudentDto dto = new StudentDto();

        // Set up the request builder to reflect input
        Http.Request request = Helpers.fakeRequest("POST", "/").bodyJson(Json.toJson(dto)).build().withTransientLang("es");

        // Easier to mock out the form factory inputs here
        Messages messages = mock(Messages.class);
        MessagesApi messagesApi = mock(MessagesApi.class);
        when(messagesApi.preferred(request)).thenReturn(messages);

        ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();

        Config config = ConfigFactory.load();
        FormFactory formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validatorFactory, config);

        // Create controller and call method under test:
        final StudentController controller = new StudentController(formFactory, repository, tokenRepository);

        Result stage = controller.addStudent(request);
        String result = contentAsString(stage);

        ApiError error = Json.fromJson(Json.parse(result), ApiError.class);

        assertEquals(400, stage.status());
        assertEquals("Invalid json object", error.getMessage());
    }

    @Test
    public void checkAddUserInvalidDate() {
        // Don't need to be this involved in setting up the mock, but for demo it works:
        StudentRepository repository = mock(StudentRepository.class);
        TokenRepository tokenRepository = mock(TokenRepository.class);

        Skill skill = new Skill("Java");

        StudentDto dto = new StudentDto();
        dto.setFirstName("Steve");
        dto.setLastName("Smith");
        dto.setDateOfBirth("01 01 1990");
        dto.setEmail("test@test.nl");
        dto.setInstitute("Fontys");
        dto.setPassword("password");
        dto.setSkills(Arrays.asList(skill));

        // Set up the request builder to reflect input
        Http.Request request = Helpers.fakeRequest("POST", "/").bodyJson(Json.toJson(dto)).build().withTransientLang("es");

        // Easier to mock out the form factory inputs here
        Messages messages = mock(Messages.class);
        MessagesApi messagesApi = mock(MessagesApi.class);
        when(messagesApi.preferred(request)).thenReturn(messages);

        ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();

        Config config = ConfigFactory.load();
        FormFactory formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validatorFactory, config);

        // Create controller and call method under test:
        final StudentController controller = new StudentController(formFactory, repository, tokenRepository);

        Result stage = controller.addStudent(request);
        String result = contentAsString(stage);

        ApiError error = Json.fromJson(Json.parse(result), ApiError.class);

        assertEquals(400, stage.status());
        assertEquals("Invalid date, use: yyyy-MM-dd", error.getMessage());
    }


    @Test
    public void getUser() throws ExecutionException, InterruptedException {
        TokenRepository tokenRepository = mock(TokenRepository.class);

        // Don't need to be this involved in setting up the mock, but for demo it works:
        StudentRepository repository = mock(StudentRepository.class);
        Student student = new Student();
        student.setFirstName("Steve");

        when(repository.getById(any())).thenReturn(supplyAsync(() -> student));

        // Set up the request builder to reflect input
        Http.Request request = Helpers.fakeRequest("GET","/users/4799bcf7-8766-426a-b238-cfc3c3b47264").build().withoutTransientLang();

        // Easier to mock out the form factory inputs here
        Messages messages = mock(Messages.class);
        MessagesApi messagesApi = mock(MessagesApi.class);
        when(messagesApi.preferred(request)).thenReturn(messages);

        ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();

        Config config = ConfigFactory.load();
        FormFactory formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validatorFactory, config);

        // Create controller and call method under test:
        final StudentController controller = new StudentController(formFactory, repository, tokenRepository);

        Result stage = controller.getStudent("4799bcf7-8766-426a-b238-cfc3c3b47264");
        String result = contentAsString(stage);

        Student studentResult = Json.fromJson(Json.parse(result), Student.class);

        assertEquals(200, stage.status());
        assertEquals(student.getFirstName(), studentResult.getFirstName());
    }

    @Test
    public void checkGetUserInvalidId() throws ExecutionException, InterruptedException {
        // Don't need to be this involved in setting up the mock, but for demo it works:
        StudentRepository repository = mock(StudentRepository.class);
        TokenRepository tokenRepository = mock(TokenRepository.class);


        // Set up the request builder to reflect input
        Http.Request request = Helpers.fakeRequest("GET","/users/4799bcf7-8766-426a-b238-cfc3c3b47264").build().withoutTransientLang();

        // Easier to mock out the form factory inputs here
        Messages messages = mock(Messages.class);
        MessagesApi messagesApi = mock(MessagesApi.class);
        when(messagesApi.preferred(request)).thenReturn(messages);

        ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();

        Config config = ConfigFactory.load();
        FormFactory formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validatorFactory, config);

        // Create controller and call method under test:
        final StudentController controller = new StudentController(formFactory, repository, tokenRepository);

        Result stage = controller.getStudent("01234");

        assertEquals(400, stage.status());

    }
}
