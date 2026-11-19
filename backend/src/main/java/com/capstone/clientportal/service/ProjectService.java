package com.capstone.clientportal.service;

import com.capstone.clientportal.entity.Project;
import com.capstone.clientportal.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projects;
    public ProjectService(ProjectRepository projects) { this.projects = projects; }

    public List<Project> getApprovedProjects(String term) {
        return projects.findByStatusAndTerm("APPROVED", term);
    }

    /** US10: prevent students from guessing IDs of pending or other-term projects. */
    public Project getApprovedProject(Long id, String term) {
        return projects.findByIdAndStatusAndTerm(id, "APPROVED", term)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }
}
