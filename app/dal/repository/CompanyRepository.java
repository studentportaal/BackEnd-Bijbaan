package dal.repository;

import com.google.inject.ImplementedBy;
import dal.jpa.JPACompanyRepository;
import models.domain.Company;
import models.domain.Student;

import java.util.concurrent.CompletionStage;

@ImplementedBy(JPACompanyRepository.class)
public interface CompanyRepository {
    CompletionStage<Company> add(Company company);

    CompletionStage<Company> update(Company company);

    CompletionStage<Company> getCompanyById(String id);

    CompletionStage<Company> login(String email, String password);
}
