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

        addJobOffers();
        addStudent();
    }

    private void addStudent(){
        Student student = createStudent();
        studentRepository.add(student);
        applyForJobOffer(student);
    }

    private void applyForJobOffer(Student student){
        Application application = new Application(student, new Date(), false);
        applicationRepository.add(application);
        try {
            jobRepository.applyForJob(application, jobRepository.getAllJobOffers().toCompletableFuture().get().get(0).getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void addJobOffers() {
        LOGGER.log(Level.WARNING, "Adding mock joboffer data");

        Company company = createCompany("sundar@google.com", "Google", "Don't be evil", "Mountain View", 1, "94040", "Googleplex");
        Company company2 = createCompany("bill@microsoft.com", "Microsoft", "Empowering us all", "Redmond", 1, "40024", "Microsoft road");
        Company company3 = createCompany("jeff@amazon.com", "Amazon", "Work Hard. Have Fun. Make History.", "Seattle", 1, "133769", "Amazon river");
        Company company4 = createCompany("elon@spacex.com", "SpaceX", "Space Exploration Technologies", "Hawthorne", 1, "42351", "Milky way");
        Company company5 = createCompany("pieter@coolblue.nl", "Coolblue", "Alles voor een glimlach", "Rotterdam", 1, "5133AA", "Blauwweg");

            companyRepository.add(company);
            companyRepository.add(company2);
            companyRepository.add(company3);
            companyRepository.add(company4);
            companyRepository.add(company5);

        Skill javaSkill = new Skill("Java");
        Skill cSkill  = new Skill("C#");
        Skill goSkill = new Skill("Go");
        Skill pSkill = new Skill("Python");
        Skill cssSkill = new Skill("CSS");
        Skill htmlSkill = new Skill("HTML");

        try {
            javaSkill = skillRepository.add(javaSkill).toCompletableFuture().get();
            cSkill = skillRepository.add(cSkill).toCompletableFuture().get();
            goSkill = skillRepository.add(goSkill).toCompletableFuture().get();
            pSkill = skillRepository.add(pSkill).toCompletableFuture().get();
            cssSkill = skillRepository.add(cssSkill).toCompletableFuture().get();
            htmlSkill = skillRepository.add(htmlSkill).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }



        JobOffer jobOffer = createJobOffer("Senior software developer", new ArrayList<>(),company,goSkill);
        JobOffer jobOffer2 = createJobOffer("PHP developer", new ArrayList<>(),company2,htmlSkill);
        JobOffer jobOffer3 = createJobOffer("Java EE developer",new ArrayList<>(),company3,javaSkill);
        JobOffer jobOffer4 = createJobOffer("ASP.NET Core developer",new ArrayList<>(),company4,cSkill);
        JobOffer jobOffer5 = createJobOffer("Junior front end developer",new ArrayList<>(),company5,cssSkill);

        jobRepository.addJobOffer(jobOffer);
        jobRepository.addJobOffer(jobOffer2);
        jobRepository.addJobOffer(jobOffer3);
        jobRepository.addJobOffer(jobOffer4);
        jobRepository.addJobOffer(jobOffer5);


        JobOffer jobOffer6 = new JobOffer();
        jobOffer6.setFunction("Developer");
        jobOffer6.setInformation("Junior developer positie");
        jobOffer6.setLocation("Hoofdkantoor");
        jobOffer6.setTitle("Software developer");
        jobOffer6.setSalary(2300);
        jobOffer6.setApplications(new ArrayList<>());
        jobOffer6.setCompany(company2);
        jobRepository.addJobOffer(jobOffer6);
    }

    private Company createCompany(String email, String name, String description, String city, int housenumber, String postalCode, String streetname) {
        Company company = new Company();
        company.setEmail(email);

        byte[] salt = PasswordHelper.generateSalt();
        byte[] password = PasswordHelper.generateHash(salt, "password");

        company.setSalt(salt);
        company.setPassword(password);

        company.setName(name);
        company.setDescription(description);
        company.setCity(city);
        company.setHousenumber(housenumber);
        company.setPostalcode(postalCode);
        company.setStreetname(streetname);
        company.setRoles(new HashSet<>(Arrays.asList(Role.USER, Role.COMPANY)));

        return company;
    }

    private JobOffer createJobOffer(String title, List<Application> applications, Company company, Skill skill){
        JobOffer jobOffer = new JobOffer();
        jobOffer.setFunction("Software developer");
        jobOffer.setInformation("Op zoek naar een software engineer om het team uit te breiden.");
        jobOffer.setLocation("Hoofdkantoor");
        jobOffer.setTitle(title);
        jobOffer.setSalary(4500);
        jobOffer.setApplications(applications);
        jobOffer.setCompany(company);
        jobOffer.setSkills(Arrays.asList(skill));
        jobOffer.setTopOfTheDay(new Date());
        return jobOffer;
    }

    private Student createStudent(){
        Student student = new Student();
        student.setEmail("student@fontys.nl");
        byte[] salt = PasswordHelper.generateSalt();
        byte[] password = PasswordHelper.generateHash(salt, "password");
        student.setSalt(salt);
        student.setPassword(password);
        student.setLastName("Achternaam");
        student.setFirstName("Voornaam");
        student.setInstitute("Fontys");
        student.setDateOfBirth(new Date());
        student.setRoles(new HashSet<>(Arrays.asList(Role.USER,Role.STUDENT)));
        return student;
    }
}
