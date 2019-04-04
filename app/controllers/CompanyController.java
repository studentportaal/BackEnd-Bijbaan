package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.repository.CompanyRepository;
import models.api.ApiError;
import models.domain.Company;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

import static play.libs.Json.toJson;

public class CompanyController extends Controller {

    private final FormFactory formFactory;
    private final CompanyRepository companyRepository;

    @Inject
    public CompanyController(FormFactory formFactory, CompanyRepository companyRepository) {
        this.formFactory = formFactory;
        this.companyRepository = companyRepository;
    }

    @SuppressWarnings("Duplicates")
    @BodyParser.Of(BodyParser.Json.class)
    public Result addCompany(final Http.Request request) {
        Form<Company> companyValidator = formFactory.form(Company.class).bindFromRequest(request);

        if (companyValidator.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json format")));
        }
        JsonNode json = request.body().asJson();
        Company company = Json.fromJson(json, Company.class);
        companyRepository.add(company);
        return created(json);
    }

    @SuppressWarnings("Duplicates")
    public Result updateCompany(final Http.Request request) {
        Form<Company> companyValidator = formFactory.form(Company.class).bindFromRequest(request);

        if (companyValidator.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json format")));
        }
        JsonNode json = request.body().asJson();
        Company company = Json.fromJson(json, Company.class);
        companyRepository.update(company);
        return ok(json);
    }
}
