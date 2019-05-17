package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dal.repository.CompanyRepository;
import dal.repository.TokenRepository;
import models.api.ApiError;
import models.authentication.AuthenticateAction;
import models.authentication.AuthenticationToken;
import models.domain.Company;
import models.domain.Role;
import models.dto.CompanyDto;
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

import javax.persistence.NoResultException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.*;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.test.Helpers.contentAsString;

public class CompanyControllerTest {

    private CompanyRepository repository;
    private TokenRepository tokenRepository;
    private Company company;
    private CompanyDto companyDto;
    private Http.Request request;
    private Messages messages;
    private MessagesApi messagesApi;
    private ValidatorFactory validatorFactory;
    private Config config;
    private FormFactory formFactory;

    @Before
    public void setUp() throws Exception {
        repository = mock(CompanyRepository.class);
        tokenRepository = mock(TokenRepository.class);
        company = new Company();
        companyDto = new CompanyDto();
        messages = mock(Messages.class);
        messagesApi = mock(MessagesApi.class);
        validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        config = ConfigFactory.load();
        formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validatorFactory, config);

        company.setEmail("test@company.nl");
        company.setName("Test COMPANY");
        company.setCity("Eindhoven");
        company.setStreetname("Rachelsmolen");
        company.setHousenumber(1);
        company.setPostalcode("5612 MA");
        company.setDescription("This is a test company");

        companyDto.setEmail("test@company.nl");
        companyDto.setName("Test COMPANY");
        companyDto.setCity("Eindhoven");
        companyDto.setStreetName("Rachelsmolen");
        companyDto.setHouseNumber("1");
        companyDto.setPostalCode("5612 MA");
        companyDto.setDescription("This is a test company");
        companyDto.setPassword("password");

    }

    @Test
    public void getCompanyById() {
        Company company = new Company();
        company.setName("testinfo");

        when(repository.getCompanyById("abc")).thenReturn(supplyAsync(() -> company));

        final CompanyController controller = new CompanyController(formFactory, repository, tokenRepository);

        Company sameCompany = Json.fromJson(Json.parse(contentAsString(controller.getCompanyById("abc"))), Company.class);
        assertEquals("testinfo", sameCompany.getName());
    }

    @Test
    public void addCompany() {
        when(repository.add(any())).thenReturn(supplyAsync(() -> company));

        request = Helpers.fakeRequest("POST", "/")
                .bodyJson(Json.toJson(companyDto)).build().withTransientLang("es");

        when(messagesApi.preferred(request)).thenReturn(messages);

        final CompanyController controller = new CompanyController(formFactory, repository, tokenRepository);

        Result stage = controller.addCompany(request);
        String result = contentAsString(stage);

        Company companyResult = Json.fromJson(Json.parse(result), Company.class);

        assertEquals(201, stage.status());
        assertEquals(company.getName(), companyResult.getName());
    }

    @Test
    public void addCompanyInvalidObject() {
        companyDto.setStreetName(null);
        when(repository.add(any())).thenReturn(supplyAsync(() -> company));

        request = Helpers.fakeRequest("POST", "/")
                .bodyJson(Json.toJson(companyDto)).build().withTransientLang("es");

        when(messagesApi.preferred(request)).thenReturn(messages);

        final CompanyController controller = new CompanyController(formFactory, repository, tokenRepository);

        Result stage = controller.addCompany(request);
        String result = contentAsString(stage);

        ApiError error = Json.fromJson(Json.parse(result), ApiError.class);

        assertEquals(400, stage.status());
        assertEquals("Invalid json format", error.getMessage());
    }

    @Test
    public void addCompanyInvalidHouseNumber() {
        companyDto.setHouseNumber("5a");
        when(repository.add(any())).thenReturn(supplyAsync(() -> company));

        request = Helpers.fakeRequest("POST", "/")
                .bodyJson(Json.toJson(companyDto)).build().withTransientLang("es");

        when(messagesApi.preferred(request)).thenReturn(messages);

        final CompanyController controller = new CompanyController(formFactory, repository, tokenRepository);

        Result stage = controller.addCompany(request);
        String result = contentAsString(stage);

        ApiError error = Json.fromJson(Json.parse(result), ApiError.class);

        assertEquals(400, stage.status());
        assertEquals("House number must be an integer", error.getMessage());
    }

    @Test
    public void updateCompany() {
        company.setName("newTest");
        when(repository.update(any())).thenReturn(supplyAsync(() -> company));
        company.setUuid(UUID.randomUUID().toString());
        request = Helpers.fakeRequest("PUT", "/")
                .bodyJson(Json.toJson(company)).build().withTransientLang("es");

        Set<Role> roles = new HashSet<>(Arrays.asList(Role.USER, Role.COMPANY));
        company.setRoles(roles);

        request = request.addAttr(AuthenticateAction.USER, company);

        when(messagesApi.preferred(request)).thenReturn(messages);

        final CompanyController controller = new CompanyController(formFactory, repository, tokenRepository);

        Result stage = controller.updateCompany(request);
        String result = contentAsString(stage);

        Company companyResult = Json.fromJson(Json.parse(result), Company.class);

        assertEquals(200, stage.status());
        assertEquals(companyResult.getName(), "newTest");

    }

    @Test
    public void login() {
        when(repository.login(any(), any())).thenReturn(supplyAsync(() -> company));

        request = Helpers.fakeRequest("POST", "/")
                .bodyJson(Json.toJson(companyDto)).build();

        when(messagesApi.preferred(request)).thenReturn(messages);
        AuthenticationToken authenticationToken = new AuthenticationToken(company);
        authenticationToken.setStart(new Date());
        when(tokenRepository.createToken(any(Company.class))).thenReturn(supplyAsync(() -> authenticationToken));

        final CompanyController controller = new CompanyController(formFactory, repository, tokenRepository);

        Result stage = controller.login(request);
        String result = contentAsString(stage);


        assertEquals(200, stage.status());
    }

    @Test
    public void loginNonExist() {
        when(repository.login(any(), any())).thenThrow(NoResultException.class);

        request = Helpers.fakeRequest("POST", "/")
                .bodyJson(Json.toJson(companyDto)).build();

        when(messagesApi.preferred(request)).thenReturn(messages);

        final CompanyController controller = new CompanyController(formFactory, repository, tokenRepository);

        Result stage = controller.login(request);
        String result = contentAsString(stage);

        ApiError error = Json.fromJson(Json.parse(result), ApiError.class);

        assertEquals(400, stage.status());
        assertEquals("Invalid username and/or password", error.getMessage());
    }
}
