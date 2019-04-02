package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dal.repository.JobOfferRepository;
import models.api.ApiError;
import models.domain.JobOffer;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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

public class JobOfferControllerTest {

    private JobOfferRepository repository;
    private JobOffer jobOffer;
    private Http.Request request;
    private Messages messages;
    private MessagesApi messagesApi;
    private ValidatorFactory validatorFactory;
    private Config config;
    private FormFactory formFactory;

    @Before
    public void setUp() throws Exception {
        repository = mock(JobOfferRepository.class);
        jobOffer = new JobOffer();
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
    public void checkAddJobOffer(){

        jobOffer.setTitle("test joboffer");
        jobOffer.setLocation("Eindhoven");
        jobOffer.setFunction("testing");
        jobOffer.setInformation("you have to test");
        jobOffer.setSalary(500.50);

        when(repository.addJobOffer(any())).thenReturn(supplyAsync(() -> jobOffer));

        request = Helpers.fakeRequest("POST", "/")
                .bodyJson(Json.toJson(jobOffer)).build().withTransientLang("es");

        when(messagesApi.preferred(request)).thenReturn(messages);

        final JobOfferController controller = new JobOfferController(formFactory, repository);

        Result stage = controller.addJobOffer(request);
        String result = contentAsString(stage);

        JobOffer jobOfferResult = Json.fromJson(Json.parse(result), JobOffer.class);

        assertEquals(201, stage.status());
        assertEquals(jobOffer.getTitle(), jobOfferResult.getTitle());

    }

    @Test
    public void checkAddJobOfferInvalidJson(){
        jobOffer = new JobOffer();

        when(repository.addJobOffer(any())).thenReturn(supplyAsync(() -> jobOffer));

        request = Helpers.fakeRequest("POST", "/").bodyJson(Json.toJson(jobOffer)).build().withTransientLang("es");

        when(messagesApi.preferred(request)).thenReturn(messages);

        final JobOfferController controller = new JobOfferController(formFactory, repository);

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
    }

    @Test
    public void getJobOfferById() {
    }

    @Test
    public void getAllJobOffers() {
    }
}
