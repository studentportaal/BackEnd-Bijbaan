package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.repository.CompanyRepository;
import models.api.ApiError;
import models.converters.CompanyConverter;
import models.domain.Company;
import models.domain.Student;
import models.dto.CompanyDto;
import models.dto.StudentDto;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import security.PasswordHelper;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.concurrent.ExecutionException;

import static play.libs.Json.toJson;

public class CompanyController extends Controller {

    private final FormFactory formFactory;
    private final CompanyRepository companyRepository;
    private final CompanyConverter converter;

    @Inject
    public CompanyController(FormFactory formFactory, CompanyRepository companyRepository) {
        this.formFactory = formFactory;
        this.companyRepository = companyRepository;

        converter = new CompanyConverter();
    }

    public Result getCompanyById(String id) {
        try {
            Company company = companyRepository.getCompanyById(id).toCompletableFuture().get();
            if (company == null) return notFound("No Company");
            return ok(toJson(company));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

            return internalServerError(e.getLocalizedMessage());
        }
    }

    @SuppressWarnings("Duplicates")
    @BodyParser.Of(BodyParser.Json.class)
    public Result addCompany(final Http.Request request) {
        Form<CompanyDto> companyValidator = formFactory.form(CompanyDto.class).bindFromRequest(request);

        if (companyValidator.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json format")));
        }

        JsonNode json = request.body().asJson();
        CompanyDto dto = Json.fromJson(json, CompanyDto.class);

        Company company;

        try {
            company = converter.convert(dto);
        } catch(NumberFormatException e) {
            return badRequest(toJson(new ApiError<>("House number must be an integer")));
        }

        byte[] salt = PasswordHelper.generateSalt();
        byte[] password = PasswordHelper.generateHash(salt, dto.getPassword());

        company.setSalt(salt);
        company.setPassword(password);

        companyRepository.add(company);
        return created(toJson(company));
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

    @SuppressWarnings("Duplicates")
    public Result login(Http.Request request){

        JsonNode json = request.body().asJson();
        CompanyDto companyDto = Json.fromJson(json, CompanyDto.class);

        if(companyDto.getEmail() == null || companyDto.getPassword() == null || companyDto.getEmail().isEmpty() || companyDto.getPassword().isEmpty()){
            return badRequest(toJson(new ApiError<>("Invalid json format")));
        }

        try {
            Company company = companyRepository.login(companyDto.getEmail(), companyDto.getPassword()).toCompletableFuture().get();

            return ok(toJson(converter.convert(company)));
        } catch (InterruptedException | ExecutionException | NoResultException e) {
            return badRequest(toJson(new ApiError<>("Invalid username and/or password")));
        }
    }

}
