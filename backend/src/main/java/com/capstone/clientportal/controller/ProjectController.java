package com.capstone.clientportal.controller;

import com.capstone.clientportal.entity.Project;
import com.capstone.clientportal.service.ProjectService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<Project> getApprovedProjects() {

        return projectService.getApprovedProjects("Fall 2026");
    }
}