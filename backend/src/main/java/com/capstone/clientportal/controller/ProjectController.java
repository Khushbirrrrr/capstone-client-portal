package com.capstone.clientportal.controller;

import com.capstone.clientportal.entity.Project;
import com.capstone.clientportal.service.ProjectService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projects;
    @Value("${portal.default-term:Fall 2026}") private String defaultTerm;
    public ProjectController(ProjectService projects) { this.projects = projects; }

    @GetMapping
    public List<Project> list(@RequestParam(required = false) String term) {
        return projects.getApprovedProjects(term == null || term.isBlank() ? defaultTerm : term);
    }

    @GetMapping("/{id}")
    public Project detail(@PathVariable Long id, @RequestParam(required = false) String term) {
        return projects.getApprovedProject(id, term == null || term.isBlank() ? defaultTerm : term);
    }
}
