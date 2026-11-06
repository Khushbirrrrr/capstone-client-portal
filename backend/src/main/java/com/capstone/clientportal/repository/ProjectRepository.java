package com.capstone.clientportal.repository;
import com.capstone.clientportal.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatusAndTerm(String status, String term);
    Optional<Project> findByIdAndStatusAndTerm(Long id, String status, String term);
}
