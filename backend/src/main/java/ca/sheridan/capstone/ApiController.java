package ca.sheridan.capstone;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class ApiController {
 private final ProjectRepository projects;
 public ApiController(ProjectRepository projects){this.projects=projects;}
 @GetMapping("/health") public Map<String,String> health(){return Map.of("status","UP","application","Capstone Client Portal");}
 @GetMapping("/me") public Map<String,String> me(Principal principal){return Map.of("username",principal.getName(),"role",principal.getName().equals("faculty")?"FACULTY":"STUDENT");}
 @GetMapping("/projects") public List<Project> list(){return projects.findByApprovedTrueOrderByIdAsc();}
 @GetMapping("/projects/{id}") public Project details(@PathVariable Long id){return projects.findByIdAndApprovedTrue(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Approved project not found"));}
 @GetMapping("/faculty/projects") public List<Project> faculty(){return projects.findAll();}
}
