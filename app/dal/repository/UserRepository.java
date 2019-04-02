package dal.repository;

import com.google.inject.ImplementedBy;
import dal.jpa.JPAUserRepository;
import models.domain.User;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * This interface provides a non-blocking API for possibly blocking operations.
 */
@ImplementedBy(JPAUserRepository.class)
public interface UserRepository {

    CompletionStage<User> add(User user);

    CompletionStage<User> edit(User user);

    CompletionStage<Stream<User>> list();

    CompletionStage<Boolean> login(String email, String password);
}
