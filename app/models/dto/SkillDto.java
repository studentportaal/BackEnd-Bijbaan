package models.dto;

import models.domain.Skill;
import models.form.SkillAddCheck;
import models.form.SkillUpdateCheck;
import play.data.validation.Constraints;

public class SkillDto {
    @Constraints.Required(groups = SkillUpdateCheck.class)
    private String id;
    @Constraints.Required(groups = {SkillAddCheck.class, SkillUpdateCheck.class})
    private String name;

    public SkillDto() {
        // Required no-arg constructor
    }

    public SkillDto(Skill skill) {
        this.id = skill.getUuid();
        this.name = skill.getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
