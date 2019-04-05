package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.repository.JobOfferRepository;
import models.api.ApiError;
import models.domain.JobOffer;
import models.domain.User;
import models.parser.Parser;
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

import static play.libs.Json.toJson;

public class JobOfferController extends Controller {

    private final FormFactory formFactory;
    private final JobOfferRepository jobOfferRepository;

    @Inject
    public JobOfferController(FormFactory formFactory, JobOfferRepository jobOfferRepository) {
        this.formFactory = formFactory;
        this.jobOfferRepository = jobOfferRepository;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addJobOffer(final Http.Request request) {
        Form<JobOffer> jobOfferValidator = formFactory.form(JobOffer.class).bindFromRequest(request);

        if (jobOfferValidator.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json object")));
        } else {
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

    @BodyParser.Of(BodyParser.Json.class)
    public Result applyForJob(final Http.Request request, String id){
        Form<User> userValidator = formFactory.form(User.class).bindFromRequest(request);

        if(userValidator.hasErrors()){
            return badRequest(toJson(new ApiError<>("Invalid json object")));
        } else{
            JsonNode json = request.body().asJson();
            User user = Json.fromJson(json, User.class);
            jobOfferRepository.applyForJob(user, id);
            return ok();
        }
    }

    public Result getJobOfferCount() {
        try {
            return ok(toJson(jobOfferRepository.getJobOfferCount().toCompletableFuture().get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return badRequest(toJson(new ApiError<>("Oops something went wrong")));
        }
    }

    public Result getJobOfferById(String id) {
        try {
            return ok(toJson(jobOfferRepository.getJobOfferById(id).toCompletableFuture().get()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

            return internalServerError(e.getLocalizedMessage());
        }
    }


    public Result getAllJobOffers(String startNr, String amount) {

        if (startNr != null && amount != null) {
            if (Parser.stringToInt(startNr) && Parser.stringToInt(amount)) {
                try {
                    return ok(toJson(jobOfferRepository.getAllJobOffers(Integer.parseInt(startNr), Integer.parseInt(amount))
                            .toCompletableFuture()
                            .get()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                return badRequest(toJson(new ApiError<>("parameters need to be a number")));
            }
        } else {
            try {
                return ok(toJson(jobOfferRepository.getAllJobOffers().toCompletableFuture().get()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return badRequest(toJson(new ApiError<>("Oops, something went wrong")));
    }
}
