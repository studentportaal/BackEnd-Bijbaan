package dal.repository;

import com.google.inject.ImplementedBy;
import dal.jpa.JPAApplicationRepository;
import models.domain.Application;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * @author Max Meijer
 * Created on 14/05/2019
 */
@ImplementedBy(JPAApplicationRepository.class)
public interface ApplicationRepository {
    CompletionStage<Application> add(Application application);

    CompletionStage<Application> update(Application application);

    void remove(Application application);

    CompletionStage<Application> get(String id);

    CompletionStage<List<Application>> getByCompnay(String id);

    void markAccepted(String jobOfferId, String applicationId);
}
