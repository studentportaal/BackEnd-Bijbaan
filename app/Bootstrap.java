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

        Application application2 = new Application();
        application2.setApplicant(students.get(2));

        Application application3 = new Application();
        application3.setApplicant(students.get(3));

        try {
            application = applicationRepository.add(application).toCompletableFuture().get();
            application1 = applicationRepository.add(application1).toCompletableFuture().get();
            application2 = applicationRepository.add(application2).toCompletableFuture().get();
            application3 = applicationRepository.add(application3).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }



        JobOffer jobOffer = this.createJobOffer("Senior software developer", Arrays.asList(application),company,javaSkill);
        JobOffer jobOffer2 = this.createJobOffer("PHP developer",Arrays.asList(application1),company,javaSkill);
        JobOffer jobOffer3 = this.createJobOffer("Java EE developer",Arrays.asList(application2),company,javaSkill);
        JobOffer jobOffer4 = this.createJobOffer("Awesome ASP.NET stuff",new ArrayList<>(),company,javaSkill);
        JobOffer jobOffer5 = this.createJobOffer("HTML/CSS job offer",new ArrayList<>(),company,javaSkill);

        jobRepository.addJobOffer(jobOffer);
        jobRepository.addJobOffer(jobOffer2);
        jobRepository.addJobOffer(jobOffer3);
        jobRepository.addJobOffer(jobOffer4);
        jobRepository.addJobOffer(jobOffer5);

        JobOffer jobOffer1 = new JobOffer();
        jobOffer1.setFunction("Junior Software Developer");
        jobOffer1.setInformation("ASP.NET ");
        jobOffer1.setLocation("Google");
        jobOffer1.setTitle("Junior front-end developer");
        jobOffer1.setSalary(2300);
        jobOffer1.setApplications(Arrays.asList(application3));
        jobOffer1.setCompany(company);
        jobOffer1.setSkills(Arrays.asList(cSkill));
        jobRepository.addJobOffer(jobOffer1);


        JobOffer jobOffer6 = new JobOffer();
        jobOffer6.setFunction("Something");
        jobOffer6.setInformation("Junior developer positie");
        jobOffer6.setLocation("Google");
        jobOffer6.setTitle("Old topofday");
        jobOffer6.setSalary(2300);
        jobOffer6.setApplications(new ArrayList<>());
        jobOffer6.setCompany(company);
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        jobOffer1.setSkills(Arrays.asList(cSkill));
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
        company.setRoles(new HashSet<>(Arrays.asList(Role.USER, Role.COMPANY)));

        return company;
    }

    private JobOffer createJobOffer(String title, List<Application> applications, Company company, Skill skill){
        JobOffer jobOffer = new JobOffer();
        jobOffer.setFunction("Senior Software Developer");
        jobOffer.setInformation("Software maken voor geld");
        jobOffer.setLocation("Google");
        jobOffer.setTitle(title);
        jobOffer.setSalary(4500);
        jobOffer.setApplications(applications);
        jobOffer.setCompany(company);
        jobOffer.setSkills(Arrays.asList(skill));
        jobOffer.setTopOfTheDay(new Date());
        return jobOffer;
    }
}
