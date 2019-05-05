package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dal.repository.CompanyRepository;
import dal.repository.JobOfferRepository;
import models.api.ApiError;
import models.domain.Company;
import models.domain.JobOffer;
import models.domain.User;
import models.dto.JobOfferDto;
import models.dto.StudentDto;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.After;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.test.Helpers.contentAsString;

public class JobOfferControllerTest {

    private JobOfferRepository repository;
    private CompanyRepository companyRepository;

    private JobOffer jobOffer;
    private JobOfferDto jobOfferDto;
    private Company company;
    private Http.Request request;
    private Messages messages;
    private MessagesApi messagesApi;
    private ValidatorFactory validatorFactory;
    private Config config;
    private FormFactory formFactory;

    @Before
    public void setUp() throws Exception {
        repository = mock(JobOfferRepository.class);
        companyRepository = mock(CompanyRepository.class);
        jobOffer = new JobOffer();
        jobOfferDto = new JobOfferDto();
        company = new Company();
        messages = mock(Messages.class);
        messagesApi = mock(MessagesApi.class);
        validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        config = ConfigFactory.load();
        formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validatorFactory, config);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void checkAddJobOffer() {

        jobOffer.setTitle("test joboffer");
        jobOffer.setLocation("Eindhoven");
        jobOffer.setFunction("testing");
        jobOffer.setInformation("you have to test");
        jobOffer.setSalary(500.50);

        when(repository.addJobOffer(any())).thenReturn(supplyAsync(() -> jobOffer));

        request = Helpers.fakeRequest("POST", "/")
                .bodyJson(Json.toJson(jobOffer)).build().withTransientLang("es");

        when(messagesApi.preferred(request)).thenReturn(messages);

        final JobOfferController controller = new JobOfferController(formFactory, repository, companyRepository);

        Result stage = controller.addJobOffer(request);
        String result = contentAsString(stage);

        JobOffer jobOfferResult = Json.fromJson(Json.parse(result), JobOffer.class);

        assertEquals(201, stage.status());
        assertEquals(jobOffer.getTitle(), jobOfferResult.getTitle());

    }

    @Test
    public void checkAddJobOfferWithCompany() {

        company.setUuid("2eab97be-c607-4289-aeed-598450cd357c");
        company.setName("test company");
        company.setCity("Eindhoven");
        company.setDescription("A company that tests software");
        company.setStreetname("TestStreet");
        company.setPostalcode("5000TT");
        company.setHousenumber(1);

        companyRepository.add(company);
        when(companyRepository.getCompanyById(any())).thenReturn(supplyAsync(()-> company));
        jobOfferDto.setTitle("test joboffer");
        jobOfferDto.setLocation("Eindhoven");
        jobOfferDto.setFunction("testing");
        jobOfferDto.setInformation("you have to test with company");
        jobOfferDto.setSalary(500.50);
        jobOfferDto.setCompany(company.getUuid());

        when(repository.addJobOffer(any())).thenReturn(supplyAsync(() -> jobOffer));

        request = Helpers.fakeRequest("POST", "/")
                .bodyJson(Json.toJson(jobOfferDto)).build().withTransientLang("en");

        when(messagesApi.preferred(request)).thenReturn(messages);

        final JobOfferController controller = new JobOfferController(formFactory, repository, companyRepository);

        Result stage = controller.addJobOffer(request);
        String result = contentAsString(stage);

        JobOffer jobOfferResult = Json.fromJson(Json.parse(result), JobOfferDto.class).toModel(companyRepository);

        assertEquals(201, stage.status());
        assertEquals(jobOfferDto.getCompany(), jobOfferResult.getCompany().getUuid());

    }

    @Test
    public void checkAddJobOfferInvalidJson() {
        jobOffer = new JobOffer();

        when(repository.addJobOffer(any())).thenReturn(supplyAsync(() -> jobOffer));

        request = Helpers.fakeRequest("POST", "/").bodyJson(Json.toJson(jobOffer)).build().withTransientLang("es");

        when(messagesApi.preferred(request)).thenReturn(messages);

        final JobOfferController controller = new JobOfferController(formFactory, repository, companyRepository);

        Result stage = controller.addJobOffer(request);
        String result = contentAsString(stage);

        ApiError error = Json.fromJson(Json.parse(result), ApiError.class);

        assertEquals(400, stage.status());
        assertEquals("Invalid json object", error.getMessage());
    }

    @Test
    public void removeJobOffer() {
    }

    @Test
    public void updateJobOffer() {
        jobOffer = new JobOffer();

        jobOffer.setTitle("test joboffer");
        jobOffer.setLocation("Eindhoven");
        jobOffer.setFunction("testing");
        jobOffer.setInformation("you have to test");
        jobOffer.setSalary(500.50);

        when(repository.updateJobOffer(any())).thenReturn(supplyAsync(() -> jobOffer));

        request = Helpers.fakeRequest("PUT", "/")
                .bodyJson(Json.toJson(jobOffer)).build().withTransientLang("es");

        when(messagesApi.preferred(request)).thenReturn(messages);

        final JobOfferController controller = new JobOfferController(formFactory, repository, companyRepository);

        Result stage = controller.updateJobOffer(request, jobOffer.getId());
        String result = contentAsString(stage);

        JobOffer jobOfferResult = Json.fromJson(Json.parse(result), JobOffer.class);

        assertEquals(200, stage.status());
        assertEquals(jobOffer.getTitle(), jobOfferResult.getTitle());
    }

    @Test
    public void getJobOfferById() {
        JobOffer jobOffer = new JobOffer();
        jobOffer.setInformation("testinfo");

        when(repository.getJobOfferById("abc")).thenReturn(supplyAsync(() -> jobOffer));


        final JobOfferController controller = new JobOfferController(formFactory, repository, companyRepository);

        JobOffer sameJobOffer = Json.fromJson(Json.parse(contentAsString(controller.getJobOfferById("abc"))), JobOffer.class);

        assertEquals(sameJobOffer.getInformation(), "testinfo");
    }

    @Test
    public void getAllJobOffers() {
        List<JobOffer> jobOfferList = new ArrayList<>();

        for (int x = 0; x < 10; x++) {
            jobOfferList.add(jobOffer);
        }

        when(repository.getAllJobOffers()).thenReturn(supplyAsync(() -> jobOfferList));
        request = Helpers.fakeRequest("GET", "/").build().withTransientLang("es");
        when(messagesApi.preferred(request)).thenReturn(messages);

        final JobOfferController controller = new JobOfferController(formFactory, repository, companyRepository);
        Result stage = controller.getAllJobOffers(null, null);
        String result = contentAsString(stage);

        List<JobOffer> jobOffers = new ArrayList<>();
        try {
            jobOffers = new ObjectMapper().readValue(result, TypeFactory.defaultInstance().constructCollectionType(List.class, JobOffer.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(200, stage.status());
        assertEquals(10, jobOffers.size());
    }

    @Test
    public void getJobOfferCount() {
        List<JobOffer> jobOfferList = new ArrayList<>();
        jobOfferList.add(jobOffer);
        for (int x = 0; x < 10; x++) {
            jobOfferList.add(jobOffer);
        }

        when(repository.getJobOfferCount()).thenReturn(supplyAsync(() -> String.valueOf(jobOfferList.size())));
        request = Helpers.fakeRequest("GET", "/").build().withTransientLang("es");
        when(messagesApi.preferred(request)).thenReturn(messages);

        final JobOfferController controller = new JobOfferController(formFactory, repository, companyRepository);
        Result stage = controller.getJobOfferCount();
        String result = contentAsString(stage);

        assertTrue(result.contains("11"));
    }

    @Test
    public void getAllJobOffersPaginated() {

        List<JobOffer> jobOfferList = new ArrayList<>();
        for (int x = 0; x < 300; x++) {
            jobOfferList.add(jobOffer);
        }

        when(repository.getAllJobOffers(0, 100)).thenReturn(supplyAsync(() -> {
            List<JobOffer> paginatedList = new ArrayList<>();
            for (int x = 0; x < 100; x++) {
                paginatedList.add(jobOfferList.get(x));
            }
            return paginatedList;
        }));

        request = Helpers.fakeRequest("GET", "/").build().withTransientLang("es");
        when(messagesApi.preferred(request)).thenReturn(messages);

        final JobOfferController controller = new JobOfferController(formFactory, repository, companyRepository);
        Result stage = controller.getAllJobOffers("0", "100");
        String result = contentAsString(stage);

        List<JobOffer> jobOffers = new ArrayList<>();
        try {
            jobOffers = new ObjectMapper().readValue(result, TypeFactory.defaultInstance().constructCollectionType(List.class, JobOffer.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(200, stage.status());
        assertEquals(100, jobOffers.size());
    }

    @Test
    public void getAllJobOffersPaginatedInvalidParameters() {
        List<JobOffer> jobOfferList = new ArrayList<>();

        when(repository.getAllJobOffers(0, 5)).thenReturn(supplyAsync(() -> jobOfferList));
        request = Helpers.fakeRequest("GET", "/").build().withTransientLang("es");

        final JobOfferController controller = new JobOfferController(formFactory, repository, companyRepository);
        Result stage = controller.getAllJobOffers("test", "henk");
        String result = contentAsString(stage);

        ApiError error = Json.fromJson(Json.parse(result), ApiError.class);

        assertEquals(400, stage.status());
        assertEquals("parameters need to be a number", error.getMessage());

        Result stage2 = controller.getAllJobOffers("20", "test");
        String result2 = contentAsString(stage);

        ApiError error2 = Json.fromJson(Json.parse(result2), ApiError.class);

        assertEquals(400, stage2.status());
        assertEquals("parameters need to be a number", error2.getMessage());

        Result stage3 = controller.getAllJobOffers("test", "100");
        String result3 = contentAsString(stage3);

        ApiError error3 = Json.fromJson(Json.parse(result3), ApiError.class);

        assertEquals(400, stage3.status());
        assertEquals("parameters need to be a number", error3.getMessage());
    }

    @Test
    public void applyForJob(){
        // TODO fix this test.
    }
}
