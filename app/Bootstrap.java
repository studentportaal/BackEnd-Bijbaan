import dal.repository.CompanyRepository;
import dal.repository.JobOfferRepository;
import dal.repository.UserRepository;
import models.domain.Company;
import models.domain.JobOffer;
import models.domain.User;
import play.Environment;
import play.inject.ApplicationLifecycle;
import security.PasswordHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Max Meijer
 * Created on 02/04/2019
 */
@Singleton
public class Bootstrap {
    private final Logger LOGGER = Logger.getLogger(Bootstrap.class.getName());
    private final UserRepository userRepository;
    private final JobOfferRepository jobRepository;
    private final CompanyRepository companyRepository;

    @Inject
    public Bootstrap(ApplicationLifecycle lifecycle,
                     Environment environment,
                     UserRepository userRepository,
                     JobOfferRepository jobRepository,
                     CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;

        addJobOffers();
        addUsers();
        addCompanies();
    }

    private void addUsers() {
        LOGGER.log(Level.WARNING, "Adding mock user data");
        for (int i = 0; i < 9; i++) {
            User user = new User();
            user.setEmail("test" + i + "@test.nl");
            user.setFirstName((Integer.toString(i)));
            user.setLastName("Test");
            user.setInstitute("Fontys");
            user.setDateOfBirth(new Date());

            byte[] salt = PasswordHelper.generateSalt();
            byte[] password = PasswordHelper.generateHash(salt, "password");

            user.setSalt(salt);
            user.setPassword(password);

            userRepository.add(user);
        }
    }

    private void addJobOffers() {
        LOGGER.log(Level.WARNING, "Adding mock joboffer data");
        for (int i = 0; i < 9; i++) {
            JobOffer jobOffer = new JobOffer();
            jobOffer.setFunction("Senior Software Verneuker");
            jobOffer.setInformation("Software verneuken voor geld.");
            jobOffer.setLocation("Fontys kelder");
            jobOffer.setTitle("Software verneuken voor geld!");
            jobOffer.setSalary(83286487648648164.3);

            jobRepository.addJobOffer(jobOffer);
        }
    }

    private void addCompanies() {
        LOGGER.log(Level.WARNING, "Adding mock joboffer data");
        for (int i = 0; i < 9; i++) {
            Company company = new Company();

            company.setName("Company " + i);
            companyRepository.add(company);
        }
    }
}
