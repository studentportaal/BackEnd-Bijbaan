package dal.repository;

import com.google.inject.ImplementedBy;
import dal.jpa.JPASkillRepository;
import models.domain.Skill;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(JPASkillRepository.class)
public interface SkillRepository {
    CompletionStage<Skill> add(Skill skill);
    CompletionStage<Skill> update(Skill skill);
    void remove(Skill skill);
    CompletionStage<List<Skill>> get();
    CompletionStage<Skill> get(String id);
    CompletionStage<List<Skill>> search(String query);
}
