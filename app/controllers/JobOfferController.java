package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.JobOfferRepository;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class JobOfferController extends Controller {

    private final JobOfferRepository jobOfferRepository;
    private final HttpExecutionContext ec;

    @Inject
    public JobOfferController(JobOfferRepository jobOfferRepository, HttpExecutionContext ec) {
        this.jobOfferRepository = jobOfferRepository;
        this.ec = ec;
    }

    public CompletionStage<Result> addJobOffer(Http.Request request) {
        return null;
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

    public Result getAllJobOffers(){
        try {

            return ok(toJson(jobOfferRepository.getAllJobOffers()
                    .toCompletableFuture()
                    .get()
                    .collect(Collectors.toList())));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ok();
    }
}
