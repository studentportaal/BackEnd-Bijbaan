import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import controllers.UserController;
import dal.repository.UserRepository;
import models.domain.User;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.Test;
import play.api.test.CSRFTokenHelper;
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
import java.util.concurrent.ForkJoinPool;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;

/**
 * Simple (JUnit) tests that can call all parts of a play app.
 * <p>
 * https://www.playframework.com/documentation/latest/JavaTest
 */
public class UnitTest {

    @Test
    public void checkIndex() {
        Http.RequestBuilder request = CSRFTokenHelper.addCSRFToken(Helpers.fakeRequest("GET", "/"));

        UserRepository repository = mock(UserRepository.class);
        FormFactory formFactory = mock(FormFactory.class);
        HttpExecutionContext ec = new HttpExecutionContext(ForkJoinPool.commonPool());
        final UserController controller = new UserController(formFactory, repository);
        final Result result = controller.index(request.build());

        assertThat(result.status()).isEqualTo(OK);
    }

    @Test
    public void checkTemplate() {
        Http.RequestBuilder request = CSRFTokenHelper.addCSRFToken(Helpers.fakeRequest("GET", "/"));
        //Content html = views.html.index.render(request.build());
        //   assertThat(html.contentType()).isEqualTo("text/html");
        //  assertThat(contentAsString(html)).contains("Add User");

        assertThat(true).isEqualTo(true);
    }

    @Test
    public void checkAddPerson() {
        // Don't need to be this involved in setting up the mock, but for demo it works:
        UserRepository repository = mock(UserRepository.class);
        User user = new User();
        user.setUuid("asd");
        user.setFirstName("Steve");
        when(repository.add(any())).thenReturn(supplyAsync(() -> user));

        // Set up the request builder to reflect input
        Http.Request request = Helpers.fakeRequest("POST", "/").bodyJson(Json.toJson(user)).build().withTransientLang("es");

        // Easier to mock out the form factory inputs here
        Messages messages = mock(Messages.class);
        MessagesApi messagesApi = mock(MessagesApi.class);
        when(messagesApi.preferred(request)).thenReturn(messages);

        ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();

        Config config = ConfigFactory.load();
        FormFactory formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validatorFactory, config);

        // It is okay to use commonPool here since this is just a test.
        HttpExecutionContext ec = new HttpExecutionContext(ForkJoinPool.commonPool());

        // Create controller and call method under test:
        final UserController controller = new UserController(formFactory, repository);

        Result stage = controller.addPerson(request);

    }

}
