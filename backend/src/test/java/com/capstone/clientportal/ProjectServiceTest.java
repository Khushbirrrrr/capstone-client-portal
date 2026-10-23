
package com.capstone.clientportal;

import com.capstone.clientportal.entity.Project;
import com.capstone.clientportal.repository.ProjectRepository;
import com.capstone.clientportal.service.ProjectService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project approvedProject;

    @BeforeEach
    void setUp() {
        approvedProject = new Project();
    }

    @Test
    void shouldReturnApprovedProjects() {
        when(projectRepository.findByStatusAndTerm(
                "APPROVED", "Fall 2026"))
                .thenReturn(List.of(approvedProject));

        List<Project> result =
                projectService.getApprovedProjects("Fall 2026");

        assertEquals(1, result.size());
        assertSame(approvedProject, result.get(0));

        verify(projectRepository).findByStatusAndTerm(
                "APPROVED", "Fall 2026");
    }

    @Test
    void shouldReturnEmptyListWhenNoProjectsApproved() {
        when(projectRepository.findByStatusAndTerm(
                "APPROVED", "Fall 2026"))
                .thenReturn(List.of());

        List<Project> result =
                projectService.getApprovedProjects("Fall 2026");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldOnlyRequestApprovedStatus() {
        when(projectRepository.findByStatusAndTerm(
                "APPROVED", "Fall 2026"))
                .thenReturn(List.of());

        projectService.getApprovedProjects("Fall 2026");

        verify(projectRepository).findByStatusAndTerm(
                "APPROVED", "Fall 2026");

        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void shouldFilterByRequestedTerm() {
        when(projectRepository.findByStatusAndTerm(
                "APPROVED", "Winter 2027"))
                .thenReturn(List.of());

        projectService.getApprovedProjects("Winter 2027");

        verify(projectRepository).findByStatusAndTerm(
                "APPROVED", "Winter 2027");
    }

    @Test
    void shouldReturnMultipleApprovedProjects() {
        Project secondProject = new Project();
        Project thirdProject = new Project();

        when(projectRepository.findByStatusAndTerm(
                "APPROVED", "Fall 2026"))
                .thenReturn(List.of(
                        approvedProject,
                        secondProject,
                        thirdProject
                ));

        List<Project> result =
                projectService.getApprovedProjects("Fall 2026");

        assertEquals(3, result.size());
    }
}
