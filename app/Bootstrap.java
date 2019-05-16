import dal.repository.CompanyRepository;
import dal.repository.JobOfferRepository;
import dal.repository.StudentRepository;
import models.domain.Company;
import models.domain.JobOffer;
import models.domain.Student;
import models.domain.User;
import play.Environment;
import play.inject.ApplicationLifecycle;
import security.PasswordHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Max Meijer
 * Created on 02/04/2019
 */
@Singleton
public class Bootstrap {
    private final Logger LOGGER = Logger.getLogger(Bootstrap.class.getName());
    private final StudentRepository studentRepository;
    private final JobOfferRepository jobRepository;
    private final CompanyRepository companyRepository;

    @Inject
    public Bootstrap(ApplicationLifecycle lifecycle,
                     Environment environment,
                     StudentRepository studentRepository,
                     JobOfferRepository jobRepository,
                     CompanyRepository companyRepository) {
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;

        addUsers();
        addJobOffers();
    }

    private void addUsers() {
        LOGGER.log(Level.WARNING, "Adding mock user data");
        for (int i = 0; i < 9; i++) {
            Student s = new Student();
            s.setEmail("test" + i + "@test.nl");
            s.setFirstName((Integer.toString(i)));
            s.setLastName("Test");
            s.setInstitute("Fontys");
            s.setDateOfBirth(new Date());

            byte[] salt = PasswordHelper.generateSalt();
            byte[] password = PasswordHelper.generateHash(salt, "password");

            s.setSalt(salt);
            s.setPassword(password);

            studentRepository.add(s);
        }
    }

    private void addJobOffers() {
        LOGGER.log(Level.WARNING, "Adding mock joboffer data");

        Company company = createCompany("test@bedrijf.nl");
        Company company2 = createCompany("test2@bedrijf.nl");
        company2.setName("Testcompany");

        try {
            companyRepository.add(company).toCompletableFuture().get();
            companyRepository.add(company2).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        List<Student> students = new ArrayList<>();
        try {
            students = studentRepository.list().toCompletableFuture().get().collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        JobOffer jobOffer = this.createJobOffer("Senior software developer",students,company);
        JobOffer jobOffer2 = this.createJobOffer("PHP developer",students,company);
        JobOffer jobOffer3 = this.createJobOffer("Java EE developer",students,company);
        JobOffer jobOffer4 = this.createJobOffer("Awesome ASP.NET stuff",students,company);
        JobOffer jobOffer5 = this.createJobOffer("HTML/CSS job offer",students,company);
        jobRepository.addJobOffer(jobOffer);
        jobRepository.addJobOffer(jobOffer2);
        jobRepository.addJobOffer(jobOffer3);
        jobRepository.addJobOffer(jobOffer4);
        jobRepository.addJobOffer(jobOffer5);

        JobOffer jobOffer1 = new JobOffer();
        jobOffer1.setFunction("Junior Software Developer");
        jobOffer1.setInformation("Junior developer positie");
        jobOffer1.setLocation("Google");
        jobOffer1.setTitle("Junior front-end developer");
        jobOffer1.setSalary(2300);
        jobOffer1.setApplicants(students);
        jobOffer1.setCompany(company);
        jobRepository.addJobOffer(jobOffer1);


        JobOffer jobOffer6 = new JobOffer();
        jobOffer6.setFunction("Something");
        jobOffer6.setInformation("Junior developer positie");
        jobOffer6.setLocation("Google");
        jobOffer6.setTitle("Old topofday");
        jobOffer6.setSalary(2300);
        jobOffer6.setApplicants(students);
        jobOffer6.setCompany(company);
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        jobOffer6.setTopOfTheDay(new Date(System.currentTimeMillis() - (3 * DAY_IN_MS)));
        jobRepository.addJobOffer(jobOffer6);
    }

    private Company createCompany(String email) {
        Company company = new Company();
        company.setEmail(email);

        byte[] salt = PasswordHelper.generateSalt();
        byte[] password = PasswordHelper.generateHash(salt, "password");

        company.setSalt(salt);
        company.setPassword(password);

        company.setName("MegaHard");
        company.setDescription("Embrace, extend, extinguish.");
        company.setCity("Bluemont");
        company.setHousenumber(10000);
        company.setPostalcode("4242XL");
        company.setStreetname("One MegaHard Way");

        return company;
    }

    private JobOffer createJobOffer(String title, List<Student> students, Company company){
        JobOffer jobOffer = new JobOffer();
        jobOffer.setFunction("Senior Software Developer");
        jobOffer.setInformation("Software maken voor geld");
        jobOffer.setLocation("Google");
        jobOffer.setTitle(title);
        jobOffer.setSalary(4500);
        jobOffer.setApplicants(students);
        jobOffer.setCompany(company);
        jobOffer.setTopOfTheDay(new Date());
        return jobOffer;
    }
}
