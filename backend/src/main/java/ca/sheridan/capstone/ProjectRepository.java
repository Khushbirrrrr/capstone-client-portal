package ca.sheridan.capstone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ProjectRepository extends JpaRepository<Project,Long>{
 List<Project> findByApprovedTrueOrderByIdAsc();
 Optional<Project> findByIdAndApprovedTrue(Long id);
}
