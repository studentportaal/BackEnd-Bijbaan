package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.interfaces.JobOfferRepository;
import models.JobOffer;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

public class JobOfferController extends Controller {

    private final FormFactory formFactory;
    private final JobOfferRepository jobOfferRepository;
    private final HttpExecutionContext ec;

    @Inject
    public JobOfferController(FormFactory formFactory, JobOfferRepository jobOfferRepository, HttpExecutionContext ec){
        this.formFactory = formFactory;
        this.jobOfferRepository = jobOfferRepository;
        this.ec = ec;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addJobOffer(final Http.Request request){
        JsonNode json = request.body().asJson();
        JobOffer jobOffer = Json.fromJson(json, JobOffer.class);
        Form<JobOffer> jobOfferValidator = formFactory.form(JobOffer.class).bindFromRequest(request);

        if(jobOfferValidator.hasErrors()){
            return badRequest();
        }
        else{
            jobOfferRepository.addJobOffer(jobOffer);
            return created(json);
        }
    }

    public Result removeJobOffer(){
        return null;
    }

    public Result updateJobOffer(){
        return null;
    }

    public Result getJobOfferById(){
        return null;
    }
}
