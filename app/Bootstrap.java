import dal.repository.*;
import models.domain.*;
import play.Environment;
import play.inject.ApplicationLifecycle;
import security.PasswordHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Array;
import java.util.*;
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
    private final SkillRepository skillRepository;
    private final ApplicationRepository applicationRepository;

    @Inject
    public Bootstrap(ApplicationLifecycle lifecycle,
                     Environment environment,
                     StudentRepository studentRepository,
                     JobOfferRepository jobRepository,
                     CompanyRepository companyRepository,
                     SkillRepository skillRepository,
                     ApplicationRepository applicationRepository) {
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
        this.applicationRepository = applicationRepository;

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

        Skill javaSkill = new Skill("Java");
        Skill cSkill  = new Skill("C#");

        try {
            javaSkill = skillRepository.add(javaSkill).toCompletableFuture().get();
            cSkill = skillRepository.add(cSkill).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Application application = new Application();
        application.setApplicant(students.get(0));

        Application application1 = new Application();
        application1.setApplicant(students.get(1));
        try {
            application = applicationRepository.add(application).toCompletableFuture().get();
            application1 = applicationRepository.add(application1).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        JobOffer jobOffer = new JobOffer();
        jobOffer.setFunction("Senior Software Developer");
        jobOffer.setInformation("Java backend engineer");
        jobOffer.setLocation("Google");
        jobOffer.setTitle("Senior back-end engineer");
        jobOffer.setSalary(4500);
        jobOffer.setApplications(Arrays.asList(application));
        jobOffer.setCompany(company);
        jobOffer.setSkills(Arrays.asList(javaSkill));
        jobRepository.addJobOffer(jobOffer);


        JobOffer jobOffer1 = new JobOffer();
        jobOffer1.setFunction("Junior Software Developer");
        jobOffer1.setInformation("ASP.NET ");
        jobOffer1.setLocation("Google");
        jobOffer1.setTitle("Junior front-end developer");
        jobOffer1.setSalary(2300);
        jobOffer1.setApplications(Arrays.asList(application1));
        jobOffer1.setCompany(company);
        jobOffer1.setSkills(Arrays.asList(cSkill));
        jobRepository.addJobOffer(jobOffer1);
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
}
