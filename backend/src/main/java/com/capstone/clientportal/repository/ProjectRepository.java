package com.capstone.clientportal.repository;

import com.capstone.clientportal.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatusAndTerm(
        String status,
        String term
    );
}