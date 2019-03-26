package controllers;

import dal.repository.JobOfferRepository;
import play.mvc.Http;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class JobOfferController extends Controller {

    private final JobOfferRepository jobOfferRepository;

    @Inject
    public JobOfferController(JobOfferRepository jobOfferRepository) {
        this.jobOfferRepository = jobOfferRepository;
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
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return ok();
    }
}
