package dal.jpa;

import dal.context.DatabaseExecutionContext;
import dal.repository.StudentRepository;
import models.domain.Student;
import play.db.jpa.JPAApi;
import security.PasswordHelper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Provide JPA operations running inside of a thread pool sized to the connection pool
 */
public class JPAStudentRepository implements StudentRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public JPAStudentRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<Student> add(Student student) {
        return supplyAsync(() -> wrap(em -> insert(em, student)), executionContext);
    }

    @Override
    public CompletionStage<Student> edit(Student student) {
        return supplyAsync( () -> wrap(em -> update(em, student)), executionContext);
    }

    @Override
    public CompletionStage<Stream<Student>> list() {
        return supplyAsync(() -> wrap(em -> list(em)), executionContext);
    }

    @Override
    public CompletionStage<Student> login(String email, String password) {
        byte[] salt = wrap(em -> getStudentSalt(em, email));

        return supplyAsync(() -> wrap(em -> getStudentAndPassword(em, email, PasswordHelper.generateHash(salt, password))));
    }

    @Override
    public CompletionStage<Student> getById(String id) {
        return supplyAsync(()->wrap(em -> getById(em,id) ),executionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Student insert(EntityManager em, Student student) {
        em.persist(student);
        return student;
    }
    private Student getById(EntityManager em, String id){
        return em.createNamedQuery("getStudent", Student.class).setParameter("id",id).getSingleResult();
    }

    private Student update(EntityManager em, Student student){
        Student u = getById(em, student.getUuid());
        student.setSalt(u.getSalt());
        student.setPassword(u.getPassword());
        em.merge(student);
        return student;
    }

    private Stream<Student> list(EntityManager em) {
        List<Student> students = em.createNamedQuery("getStudents", Student.class).getResultList();
        return students.stream();
    }

    private Student getStudentAndPassword(EntityManager em, String email, byte[] hashedPassword){
        TypedQuery<Student> query = em.createQuery(
                "SELECT s " +
                        "FROM Student s WHERE s.email = :email AND s.password = :password", Student.class)
                .setParameter("email", email)
                .setParameter("password", hashedPassword);
        return query.getSingleResult();
    }

    @Transactional
    byte[] getStudentSalt(EntityManager em, String email) {
        TypedQuery<byte[]> query = em.createQuery("SELECT S.salt FROM Student s WHERE s.email = :email", byte[].class);
        query.setParameter("email", email);
        return query.getSingleResult();
    }
}
