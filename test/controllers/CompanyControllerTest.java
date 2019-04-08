package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dal.repository.CompanyRepository;
import models.domain.Company;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.Before;
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

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.test.Helpers.contentAsString;

public class CompanyControllerTest {

    private CompanyRepository repository;
    private Company company;
    private Http.Request request;
    private Messages messages;
    private MessagesApi messagesApi;
    private ValidatorFactory validatorFactory;
    private Config config;
    private FormFactory formFactory;

    @Before
    public void setUp() throws Exception {
        repository = mock(CompanyRepository.class);
        company = new Company();
        messages = mock(Messages.class);
        messagesApi = mock(MessagesApi.class);
        validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        config = ConfigFactory.load();
        formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validatorFactory, config);

        company.setName("Test Company");
        company.setCity("Eindhoven");
        company.setStreetname("Rachelsmolen");
        company.setHousenumber(1);
        company.setPostalcode("5612 MA");
        company.setDescription("This is a test company");
    }

    @Test
    public void getCompanyById() {
        Company company = new Company();
        company.setName("testinfo");

        when(repository.getCompanyById("abc")).thenReturn(supplyAsync(() -> company));


        final CompanyController controller = new CompanyController(formFactory, repository);

        Company sameCompany = Json.fromJson(Json.parse(contentAsString(controller.getCompanyById("abc"))), Company.class);
        assertEquals("testinfo", sameCompany.getName());
    }

    @Test
    public void addCompany() {
        when(repository.add(any())).thenReturn(supplyAsync(() -> company));

        request = Helpers.fakeRequest("POST", "/")
                .bodyJson(Json.toJson(company)).build().withTransientLang("es");

        when(messagesApi.preferred(request)).thenReturn(messages);

        final CompanyController controller = new CompanyController(formFactory, repository);

        Result stage = controller.addCompany(request);
        String result = contentAsString(stage);

        Company companyResult = Json.fromJson(Json.parse(result), Company.class);

        assertEquals(201, stage.status());
        assertEquals(company.getName(), companyResult.getName());
    }

    @Test
    public void updateCompany() {
        company.setName("newTest");
        when(repository.add(any())).thenReturn(supplyAsync(() -> company));

        request = Helpers.fakeRequest("PUT", "/")
                .bodyJson(Json.toJson(company)).build().withTransientLang("es");

        when(messagesApi.preferred(request)).thenReturn(messages);

        final CompanyController controller = new CompanyController(formFactory, repository);


        Result stage = controller.updateCompany(request);
        String result = contentAsString(stage);

        Company companyResult = Json.fromJson(Json.parse(result), Company.class);

        assertEquals(200, stage.status());
        assertEquals(company.getName(), "newTest");

    }
}
