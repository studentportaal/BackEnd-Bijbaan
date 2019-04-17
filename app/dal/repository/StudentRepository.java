package dal.repository;

import com.google.inject.ImplementedBy;
import dal.jpa.JPAStudentRepository;
import models.domain.Student;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPAStudentRepository.class)
public interface StudentRepository {

    CompletionStage<Student> add(Student user);

    CompletionStage<Student> edit(Student user);

    CompletionStage<Stream<Student>> list();

    CompletionStage<Student> login(String email, String password);

    CompletionStage<Student> getById(String id);
}
