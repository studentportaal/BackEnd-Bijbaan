package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class JobOfferController extends Controller {

    @Inject
    public JobOfferController(){

    }

    public CompletionStage<Result> addJobOffer(){
        return null;
    }

    public CompletionStage<Result> removeJobOffer(){
        return null;
    }

    public CompletionStage<Result> updateJobOffer(){
        return null;
    }

    public CompletionStage<Result> getJobOfferById(){
        return null;
    }
}
