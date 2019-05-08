package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import dal.repository.SkillRepository;
import models.api.ApiError;
import models.domain.Skill;
import models.dto.SkillDto;
import models.form.SkillAddCheck;
import models.form.SkillUpdateCheck;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.libs.Json;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

import static play.libs.Json.toJson;

public class SkillController extends Controller {
    private final FormFactory formFactory;
    private final SkillRepository skillRepository;

    @Inject
    public SkillController(FormFactory formFactory, SkillRepository skillRepository) {
        this.formFactory = formFactory;
        this.skillRepository = skillRepository;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addSkill(final Http.Request request) {
        Form<SkillDto> skillForm = formFactory.form(SkillDto.class, SkillAddCheck.class).bindFromRequest(request);

        if(skillForm.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json object")));
        }

        Skill skill = fromRequest(request);

        try {
            skill = skillRepository.add(skill).toCompletableFuture().get();
            return ok(toJson(skill));
        } catch (InterruptedException | ExecutionException e) {
            return badRequest(toJson(new ApiError<>("Skill with name " + skill.getName() + " already exists")));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result updateSkill(final Http.Request request) {
        Form<SkillDto> skillForm = formFactory.form(SkillDto.class, SkillUpdateCheck.class).bindFromRequest(request);

        if(skillForm.hasErrors()) {
            return badRequest(toJson(new ApiError<>("Invalid json object")));
        }

        Skill skill = fromRequest(request);

        try {
            skill = skillRepository.update(skill).toCompletableFuture().get();
            return ok(toJson(skill));
        } catch (InterruptedException | ExecutionException e) {
            return badRequest(toJson(new ApiError<>("Skill with name " + skill.getName() + " already exists")));
        }
    }

    public Result getAll(String query) {
        if(query != null && !query.equals("")) {
            try {
                return ok(toJson(skillRepository.search(query).toCompletableFuture().get()));
            } catch (InterruptedException | ExecutionException e) {
                return badRequest(toJson(new ApiError<>("No results found")));
            }
        }

        try {
            return ok(toJson(skillRepository.get().toCompletableFuture().get()));
        } catch (InterruptedException | ExecutionException e) {
            return badRequest(toJson(new ApiError<>("No results found")));
        }
    }

    private Skill fromRequest(Http.Request req) {
        JsonNode json = req.body().asJson();
        SkillDto skillDto = Json.fromJson(json, SkillDto.class);

        return new Skill(skillDto);
    }
}
