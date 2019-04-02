package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dal.repository.UserRepository;
import models.api.ApiError;
import models.domain.User;
import models.dto.UserDto;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.Test;
import play.data.FormFactory;
import play.data.format.Formatters;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;

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
public class UserControllerTest {

    @Test
    public void checkAddUser() {
        // Don't need to be this involved in setting up the mock, but for demo it works:
        UserRepository repository = mock(UserRepository.class);

        UserDto dto = new UserDto();
        dto.setFirstName("Steve");
        dto.setLastName("Smith");
        dto.setDateOfBirth("1990-01-01");
        dto.setEmail("test@test.nl");
        dto.setInstitute("Fontys");
        dto.setPassword("password");

        User user = new User();
        user.setFirstName("Steve");

        when(repository.add(any())).thenReturn(supplyAsync(() -> user));

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
        final UserController controller = new UserController(formFactory, repository);

        Result stage = controller.addUser(request);
        String result = contentAsString(stage);

        User userResult = Json.fromJson(Json.parse(result), User.class);

        assertEquals(200, stage.status());
        assertEquals(user.getFirstName(), userResult.getFirstName());
    }

    @Test
    public void checkAddUserInvalidObject() {
        // Don't need to be this involved in setting up the mock, but for demo it works:
        UserRepository repository = mock(UserRepository.class);

        UserDto dto = new UserDto();

        User user = new User();

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
        final UserController controller = new UserController(formFactory, repository);

        Result stage = controller.addUser(request);
        String result = contentAsString(stage);

        ApiError error = Json.fromJson(Json.parse(result), ApiError.class);

        assertEquals(400, stage.status());
        assertEquals("Invalid json object", error.getMessage());
    }

    @Test
    public void checkAddUserInvalidDate() {
        // Don't need to be this involved in setting up the mock, but for demo it works:
        UserRepository repository = mock(UserRepository.class);

        UserDto dto = new UserDto();
        dto.setFirstName("Steve");
        dto.setLastName("Smith");
        dto.setDateOfBirth("01 01 1990");
        dto.setEmail("test@test.nl");
        dto.setInstitute("Fontys");
        dto.setPassword("password");

        User user = new User();

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
        final UserController controller = new UserController(formFactory, repository);

        Result stage = controller.addUser(request);
        String result = contentAsString(stage);

        ApiError error = Json.fromJson(Json.parse(result), ApiError.class);

        assertEquals(400, stage.status());
        assertEquals("Invalid date, use: yyyy-MM-dd", error.getMessage());
    }

}
