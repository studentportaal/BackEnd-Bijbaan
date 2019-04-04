package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.repository.JobOfferRepository;
import models.domain.JobOffer;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class JobOfferController extends Controller {

    private final FormFactory formFactory;
    private final JobOfferRepository jobOfferRepository;

    @Inject
    public JobOfferController(FormFactory formFactory, JobOfferRepository jobOfferRepository){
        this.formFactory = formFactory;
        this.jobOfferRepository = jobOfferRepository;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addJobOffer(final Http.Request request){
        Form<JobOffer> jobOfferValidator = formFactory.form(JobOffer.class).bindFromRequest(request);

        if(jobOfferValidator.hasErrors()){
            return badRequest();
        }
        else{
            JsonNode json = request.body().asJson();
            JobOffer jobOffer = Json.fromJson(json, JobOffer.class);
            jobOfferRepository.addJobOffer(jobOffer);
            return created(json);
        }
    }

    public CompletionStage<Result> removeJobOffer() {
        return null;
    }

    public CompletionStage<Result> updateJobOffer() {
        return null;
    }

    public CompletionStage<Result> getJobOfferById() {
        return null;
    }

    public Result getJobOfferById(String id) {
        try {
            return ok(toJson(jobOfferRepository.getJobOfferById(id).toCompletableFuture().get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

            return internalServerError(e.getLocalizedMessage());
        }
    }


    public Result getAllJobOffers(){
        //THIS IS TEMPORARY
        JobOffer offer = new JobOffer();
        offer.setFunction("test");
        offer.setInformation("dit is een test job offer");
        offer.setLocation("test locatie");
        offer.setSalary(3000);
        offer.setTitle("Dit is een test job offer");
        jobOfferRepository.addJobOffer(offer);

        try {

            return ok(toJson(jobOfferRepository.getAllJobOffers()
                    .toCompletableFuture()
                    .get()
                    .collect(Collectors.toList())));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return ok();
    }
}
