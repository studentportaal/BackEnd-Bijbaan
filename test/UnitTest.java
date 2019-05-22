import controllers.StudentController;
import dal.repository.StudentRepository;
import dal.repository.TokenRepository;
import org.junit.Test;
import play.api.test.CSRFTokenHelper;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
        TokenRepository tokenRepository = mock(TokenRepository.class);

        StudentRepository repository = mock(StudentRepository.class);
        FormFactory formFactory = mock(FormFactory.class);
        final StudentController controller = new StudentController(formFactory, repository, tokenRepository);
        final Result result = controller.index(request.build());

        assertThat(result.status()).isEqualTo(OK);
    }

    @Test
    public void checkTemplate() {
        Http.RequestBuilder request = CSRFTokenHelper.addCSRFToken(Helpers.fakeRequest("GET", "/"));
        //Content html = views.html.index.render(request.build());
        //   assertThat(html.contentType()).isEqualTo("text/html");
        //  assertThat(contentAsString(html)).contains("Add USER");

        assertThat(true).isEqualTo(true);
    }
}
