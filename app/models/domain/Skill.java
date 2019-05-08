package models.domain;

import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name = "skill.getById", query = "FROM Skill s WHERE s.uuid = :id"),
        @NamedQuery(name = "skill.getAll", query = "FROM Skill"),
        @NamedQuery(name = "skill.search", query = "FROM Skill s WHERE s.name = :name")
})
public class Skill {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String uuid;
    @Unique
    private String name;
}
