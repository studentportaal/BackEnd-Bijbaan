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
import scala.Int;

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

    public Result getJobOfferCount(){
        try{
            return ok(toJson(jobOfferRepository.getJobOfferCount().toCompletableFuture().get()));
        } catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }

        return ok();
    }

    public Result getAllJobOffers(String startNr, String amount){
        try {
            return ok(toJson(jobOfferRepository.getAllJobOffers(Integer.parseInt(startNr), Integer.parseInt(amount))
                    .toCompletableFuture()
                    .get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return ok();
    }
}
