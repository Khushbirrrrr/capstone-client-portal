package ca.sheridan.capstone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
@SpringBootApplication
public class PortalApplication {
 public static void main(String[] args){SpringApplication.run(PortalApplication.class,args);}
 @Bean CommandLineRunner seed(ProjectRepository repository){return args -> {
  if(repository.count()==0){
   repository.save(new Project("Campus Sustainability Dashboard","Track energy consumption and sustainability initiatives across campus.","Web Development",true,"Facilities Team"));
   repository.save(new Project("Community Volunteer Matcher","Connect nonprofit organizations with student volunteers.","Community Technology",true,"Community Partner"));
   repository.save(new Project("Smart Library Booking","Reserve library study rooms and view availability.","Information Systems",true,"Library Services"));
   repository.save(new Project("Confidential Faculty Planning","Internal faculty planning project; students must not see this.","Internal",false,"Faculty"));
  }
 };}
}
