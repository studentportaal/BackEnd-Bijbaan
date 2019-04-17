package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.repository.CompanyRepository;
import dal.repository.JobOfferRepository;
import models.api.ApiError;
import models.converters.StudentConverter;
import models.domain.JobOffer;
import models.domain.User;
import models.dto.JobOfferDto;
import models.dto.StudentDto;
import models.parser.Parser;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.text.ParseException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class JobOfferController extends Controller {

    private final FormFactory formFactory;
    private final JobOfferRepository jobOfferRepository;
    private final CompanyRepository companyRepository;

    @Inject
    public JobOfferController(FormFactory formFactory, JobOfferRepository jobOfferRepository, CompanyRepository companyRepository) {
        this.formFactory = formFactory;
        this.jobOfferRepository = jobOfferRepository;
        this.companyRepository = companyRepository;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addJobOffer(final Http.Request request) {
        Form<JobOfferDto> jobOfferValidator = formFactory.form(JobOfferDto.class).bindFromRequest(request);

        if (jobOfferValidator.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json object")));
        }
        else {
            JsonNode json = request.body().asJson();
            JobOffer jobOffer = Json.fromJson(json, JobOfferDto.class).toModel(companyRepository);
            jobOfferRepository.addJobOffer(jobOffer);
            return created(json);
        }
    }

    public CompletionStage<Result> removeJobOffer() {
        return null;
    }

    public Result updateJobOffer(final Http.Request request, String id) {
        Form<JobOfferDto> jobOfferValidator = formFactory.form(JobOfferDto.class).bindFromRequest(request);
        if(jobOfferValidator.hasErrors()){
            return badRequest(toJson(new ApiError<>("Invalid json object")));
        }
        else{
            JsonNode json = request.body().asJson();
            JobOffer jobOffer = Json.fromJson(json, JobOfferDto.class).toModel(companyRepository);
            jobOfferRepository.updateJobOffer(jobOffer);
            return ok(toJson(jobOffer));

        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result applyForJob(final Http.Request request, String id){
            JsonNode json = request.body().asJson();
            StudentDto studentDto = Json.fromJson(json, StudentDto.class);
            StudentConverter c = new StudentConverter();

        try{
                User u = c.convertDtoToStudent(studentDto);
                return ok(toJson(jobOfferRepository.applyForJob(u, id).toCompletableFuture().get()));
            } catch (NoResultException e ){
                return badRequest(toJson(new ApiError<>("No result found with the given ID")));
            } catch (InterruptedException | ParseException | ExecutionException e){
                return badRequest(toJson(new ApiError<>("Oops something went wrong ")));
            }
        }


    public Result getJobOfferById(String id) {
        try {
            JobOffer jobOffer = jobOfferRepository.getJobOfferById(id).toCompletableFuture().get();
            if (jobOffer == null) return notFound("No Joboffer");
            return ok(toJson(new JobOfferDto(jobOffer)));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

            return internalServerError(e.getLocalizedMessage());
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


    public Result getAllJobOffers(String startNr, String amount) {

        if (startNr != null && amount != null) {
            if (Parser.stringToInt(startNr) && Parser.stringToInt(amount)) {
                try {
                    return ok(toJson(jobOfferRepository.getAllJobOffers(Integer.parseInt(startNr), Integer.parseInt(amount))
                            .toCompletableFuture()
                            .get().stream().map(JobOfferDto::new).collect(Collectors.toList())));
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
