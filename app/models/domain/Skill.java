package models.domain;

import models.dto.SkillDto;
import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name = "skill.getById", query = "FROM Skill s WHERE s.id = :id"),
        @NamedQuery(name = "skill.getAll", query = "FROM Skill"),
        @NamedQuery(name = "skill.search", query = "FROM Skill s WHERE s.name LIKE :name")
})
public class Skill {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @Column(unique = true)
    private String name;

    public Skill() {
        // Required no-arg constructor
    }

    public Skill(@Unique String name) {
        this.name = name.toUpperCase();
    }

    public Skill(SkillDto dto) {
        if(dto.getId() != null) {
            this.id = dto.getId();
        }

        if(dto.getName()!= null) {
            this.name = dto.getName().toUpperCase();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String uuid) {
        this.id = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
