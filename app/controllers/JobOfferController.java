package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dal.repository.ApplicationRepository;
import dal.repository.CompanyRepository;
import dal.repository.JobOfferRepository;
import dal.repository.StudentRepository;
import models.api.ApiError;
import models.authentication.Authenticate;
import models.converters.StudentConverter;
import models.domain.Application;
import models.domain.JobOffer;
import models.domain.Role;
import models.domain.Skill;
import models.domain.Student;
import models.dto.ApplicationDto;
import models.dto.JobOfferDto;
import models.dto.SkillDto;
import models.dto.StudentDto;
import models.form.JobOfferSkills;
import models.form.SkillUpdateCheck;
import models.parser.Parser;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import web.RestClient;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class JobOfferController extends Controller {

    private final FormFactory formFactory;
    private final JobOfferRepository jobOfferRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final RestClient restClient;

    @Inject
    public JobOfferController(FormFactory formFactory,
                              JobOfferRepository jobOfferRepository,
                              CompanyRepository companyRepository,
                              ApplicationRepository applicationRepository,
                              StudentRepository studentRepository,
                              RestClient restClient) {
        this.formFactory = formFactory;
        this.jobOfferRepository = jobOfferRepository;
        this.companyRepository = companyRepository;
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.restClient = restClient;
    }

    @BodyParser.Of(BodyParser.Json.class)
    @Authenticate(requiredRole = Role.COMPANY)
    public Result addJobOffer(final Http.Request request) {
        Form<JobOfferDto> jobOfferValidator = formFactory.form(JobOfferDto.class).bindFromRequest(request);

        if (jobOfferValidator.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json object")));
        } else {
            JsonNode json = request.body().asJson();
            JobOffer jobOffer = Json.fromJson(json, JobOfferDto.class).toModel(companyRepository, studentRepository);
            jobOfferRepository.addJobOffer(jobOffer);
            return created(json);
        }
    }

    public CompletionStage<Result> removeJobOffer() {
        return null;
    }

    @Authenticate(requiredRole = Role.COMPANY)
    public Result updateJobOffer(final Http.Request request, String id) {
        Form<JobOfferDto> jobOfferValidator = formFactory.form(JobOfferDto.class).bindFromRequest(request);
        if (jobOfferValidator.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json object")));
        } else {
            JsonNode json = request.body().asJson();
            JobOffer jobOffer = Json.fromJson(json, JobOfferDto.class).toModel(companyRepository, studentRepository);
            jobOfferRepository.updateJobOffer(jobOffer);
            return ok(toJson(jobOffer));

        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    @Authenticate(requiredRole = Role.STUDENT)
    public Result applyForJob(final Http.Request request, String id) {
        JsonNode json = request.body().asJson();
        ApplicationDto applicationDto = Json.fromJson(json, ApplicationDto.class);


        try {
            Application application = applicationRepository.add(applicationDto.toModel(studentRepository)).toCompletableFuture().get();
            return ok(toJson(jobOfferRepository.applyForJob(application, id).toCompletableFuture().get()));
        } catch (NoResultException e) {
            return badRequest(toJson(new ApiError<>("No result found with the given ID")));
        } catch (InterruptedException | ExecutionException e) {
            return badRequest(toJson(new ApiError<>("Oops something went wrong")));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result setSkills(final Http.Request request, String id) {
        List<Skill> skills = skillFromRequest(request);

        try {
            return ok(toJson(jobOfferRepository.setSkills(skills, id).toCompletableFuture().get()));
        } catch (NoResultException | InterruptedException | ExecutionException e) {
            return badRequest(toJson(new ApiError<>("No result found with the given ID")));
        }
    }


    @BodyParser.Of(BodyParser.Json.class)
    public Result setTopOfDay(final Http.Request request) {
        try {
            JsonNode json = request.body().asJson();
            String id = Json.mapper().readValue(json.get("id").toString(),String.class);
            return ok(toJson(jobOfferRepository.setTopOfDay(id ,new Date()).toCompletableFuture().get()));
        } catch (NoResultException | InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            return badRequest(toJson(new ApiError<>("Something went wrong when adding TopOfDay to job offer")));
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

    public Result getAllJobOffers(String startNr, String amount, String companies, boolean open, String skills, String title) {

        if (startNr != null && amount != null) {
            if (Parser.stringToInt(startNr) && Parser.stringToInt(amount)) {
                try {
                    return ok(toJson(jobOfferRepository.getAllJobOffers(Integer.parseInt(startNr), Integer.parseInt(amount), companies, open, skills, title)
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
                return ok(toJson(jobOfferRepository.getAllJobOffers().toCompletableFuture().get().stream().map(JobOfferDto::new).collect(Collectors.toList())));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return badRequest(toJson(new ApiError<>("Oops, something went wrong")));
    }

    public Result getAllTopOfDays(){
        try {
            return ok(toJson(jobOfferRepository.getAllTopOfDays().toCompletableFuture().get().stream().map(JobOfferDto::new).collect(Collectors.toList())));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return badRequest(toJson(new ApiError<>("Oops, something went wrong")));
    }

    public Result acceptApplicant(String jobOfferId, String applicationId) {
        applicationRepository.markAccepted(jobOfferId, applicationId);

        try {
            JobOffer j = jobOfferRepository.getJobOfferById(jobOfferId).toCompletableFuture().get();
            Application a = applicationRepository.get(applicationId).toCompletableFuture().get();

            restClient.createReviews(j.getCompany().getUuid(), a.getApplicant().getUuid());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return noContent();
    }

    private List<Skill> skillFromRequest(Http.Request req) {
        JsonNode json = req.body().asJson();
        JsonNode skills =  json.get("skills");

        List<SkillDto> skillSet = null;
        try {
            skillSet = Json.mapper().readValue(skills.traverse(), new TypeReference<Set<SkillDto>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return skillSet.stream()
                .map(Skill::new)
                .collect(Collectors.toList());
    }

    public Result appliedJobOffers(String id){
        try {
            return ok(toJson(jobOfferRepository.getForUser(id).toCompletableFuture().get().stream().map(JobOfferDto::new).collect(Collectors.toList())));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return badRequest();
    }
}
