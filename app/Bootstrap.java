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
        Application application = new Application(student, "Ik wil graag een baan want ik vind het bedrijf cool", new Date(), false);
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
        jobOffer6.setInformation("<h3>De standaard Lorem Ipsum passage, in gebruik sinds de 16e eeuw</h3><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p><h3>Sectie 1.10.32 van de Finibus Bonorum et Malorum, geschreven door Cicero in 45 v.Chr.</h3><p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?</p><h3>1914 vertaling door H. Rackham</h3><p>But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?</p><h3>Sectie 1.10.33 van de Finibus Bonorum et Malorum, geschreven door Cicero in 45 v.Chr.</h3><p>At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat.</p><h3>1914 vertaling door H. Rackham</h3><p>On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who fail in their duty through weakness of will, which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our power of choice is untrammelled and when nothing prevents our being able to do what we like best, every pleasure is to be welcomed and every pain avoided. But in certain circumstances and owing to the claims of duty or the obligations of business it will frequently occur that pleasures have to be repudiated and annoyances accepted. The wise man therefore always holds in these matters to this principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains.</p>");
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
        company.setRoles(new HashSet<>(Arrays.asList(Role.USER, Role.COMPANY, Role.STUDENT)));

        return company;
    }

    private JobOffer createJobOffer(String title, List<Application> applications, Company company, Skill skill){
        JobOffer jobOffer = new JobOffer();
        jobOffer.setFunction("Software developer");
        jobOffer.setInformation("<h3>De standaard Lorem Ipsum passage, in gebruik sinds de 16e eeuw</h3><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p><h3>Sectie 1.10.32 van de Finibus Bonorum et Malorum, geschreven door Cicero in 45 v.Chr.</h3><p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?</p><h3>1914 vertaling door H. Rackham</h3><p>But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?</p><h3>Sectie 1.10.33 van de Finibus Bonorum et Malorum, geschreven door Cicero in 45 v.Chr.</h3><p>At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat.</p><h3>1914 vertaling door H. Rackham</h3><p>On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who fail in their duty through weakness of will, which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our power of choice is untrammelled and when nothing prevents our being able to do what we like best, every pleasure is to be welcomed and every pain avoided. But in certain circumstances and owing to the claims of duty or the obligations of business it will frequently occur that pleasures have to be repudiated and annoyances accepted. The wise man therefore always holds in these matters to this principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains.</p>");
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
