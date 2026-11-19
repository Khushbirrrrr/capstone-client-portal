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
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock ProjectRepository projects;
    @InjectMocks ProjectService service;
    private Project approved;
    @BeforeEach void setup() {
        approved = new Project("Sample project", "Sample description", "APPROVED", "Term A");
    }
    @Test void browseOnlyApprovedInRequestedTerm() {
        when(projects.findByStatusAndTerm("APPROVED", "Term A")).thenReturn(List.of(approved));
        assertEquals(1, service.getApprovedProjects("Term A").size());
        verify(projects).findByStatusAndTerm("APPROVED", "Term A");
    }
    @Test void emptyCatalogueIsSupported() {
        when(projects.findByStatusAndTerm("APPROVED", "Term B")).thenReturn(List.of());
        assertTrue(service.getApprovedProjects("Term B").isEmpty());
    }
    @Test void approvedDetailIsAccessible() {
        when(projects.findByIdAndStatusAndTerm(7L,"APPROVED","Term A"))
                .thenReturn(Optional.of(approved));
        assertSame(approved, service.getApprovedProject(7L,"Term A"));
    }
    @Test void unpublishedOrWrongTermProjectIsNotExposed() {
        when(projects.findByIdAndStatusAndTerm(7L,"APPROVED","Term A"))
                .thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.getApprovedProject(7L,"Term A"));
    }
}
