package com.capstone.clientportal.service;

import com.capstone.clientportal.entity.Project;
import com.capstone.clientportal.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getApprovedProjects(String term) {

        return projectRepository.findByStatusAndTerm(
            "APPROVED",
            term
        );
    }
}