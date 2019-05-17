package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.repository.CompanyRepository;
import dal.repository.TokenRepository;
import models.api.ApiError;
import models.authentication.Authenticate;
import models.authentication.AuthenticateAction;
import models.authentication.AuthenticationToken;
import models.authentication.JwtEncoder;
import models.converters.CompanyConverter;
import models.domain.Company;
import models.domain.Role;
import models.domain.User;
import models.dto.CompanyDto;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static play.libs.Json.toJson;

public class CompanyController extends Controller {

    private final FormFactory formFactory;
    private final CompanyRepository companyRepository;
    private TokenRepository tokenRepository;
    private final CompanyConverter converter;

    @Inject
    public CompanyController(FormFactory formFactory, CompanyRepository companyRepository, TokenRepository tokenRepository) {
        this.formFactory = formFactory;
        this.companyRepository = companyRepository;
        this.tokenRepository = tokenRepository;

        converter = new CompanyConverter();
    }

    @Authenticate(requiredRole = Role.USER)
    public Result getCompanyById(String id) {
        try {
            Company company = companyRepository.getCompanyById(id).toCompletableFuture().get();
            if (company == null) return notFound("No COMPANY");
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
        Set<Role> roles = new HashSet<>(Arrays.asList(Role.USER, Role.COMPANY));
        company.setRoles(roles);

        companyRepository.add(company);
        return created(toJson(company));
    }

    @Authenticate(requiredRole = Role.COMPANY)
    @SuppressWarnings("Duplicates")
    public Result updateCompany(final Http.Request request) {
        User user = request.attrs().get(AuthenticateAction.USER);

        Form<Company> companyValidator = formFactory.form(Company.class).bindFromRequest(request);

        if (companyValidator.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json format")));
        }
        JsonNode json = request.body().asJson();
        Company company = Json.fromJson(json, Company.class);

        if (!user.getRoles().contains(Role.ADMINISTRATOR) && user.getUuid() != company.getUuid()) {
            return unauthorized("This is not your company, buddy.");
        }

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

            CompletionStage<AuthenticationToken> token = tokenRepository.createToken(company);

            String jwt = JwtEncoder.toJWT(token.toCompletableFuture().get());

            return ok(toJson(jwt));
        } catch (InterruptedException | ExecutionException | NoResultException e) {
            return badRequest(toJson(new ApiError<>("Invalid username and/or password")));
        }
    }

    public Result getAllCompanies(){
        try {
            return ok(toJson(companyRepository.getAllCompanies().toCompletableFuture().get()));
        } catch (InterruptedException | ExecutionException e) {
            return internalServerError(toJson(new ApiError<>("Something went wrong")));
        }
    }
}
