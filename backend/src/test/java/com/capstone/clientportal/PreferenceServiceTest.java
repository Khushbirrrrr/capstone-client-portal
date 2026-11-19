package com.capstone.clientportal;

import com.capstone.clientportal.entity.AppUser;
import com.capstone.clientportal.entity.Project;
import com.capstone.clientportal.entity.RankedPreference;
import com.capstone.clientportal.repository.PreferenceRepository;
import com.capstone.clientportal.repository.ProjectRepository;
import com.capstone.clientportal.repository.UserRepository;
import com.capstone.clientportal.service.PreferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreferenceServiceTest {
    @Mock UserRepository users;
    @Mock ProjectRepository projects;
    @Mock PreferenceRepository preferences;
    @InjectMocks PreferenceService service;
    private AppUser student;
    private Project first;
    private Project second;
    private String username;

    @BeforeEach void setup() {
        username = "test-" + UUID.randomUUID().toString().substring(0, 8);
        student = new AppUser(); student.setId(42L); student.setUsername(username);
        student.setRole("STUDENT"); student.setActive(true);
        first = new Project("First", "A", "APPROVED", "Term A");
        second = new Project("Second", "B", "APPROVED", "Term A");
        ReflectionTestUtils.setField(first, "id", 10L);
        ReflectionTestUtils.setField(second, "id", 20L);
    }
    @Test void returnsOwnRankingsInOrder() {
        when(users.findByUsername(username)).thenReturn(Optional.of(student));
        when(preferences.findByUserIdAndTermOrderByRankOrderAsc(42L,"Term A"))
                .thenReturn(List.of(new RankedPreference(student,first,"Term A",1,Instant.now())));
        var result = service.list(username,"Term A");
        assertEquals(10L,result.get(0).projectId()); assertEquals(1,result.get(0).rank());
        verify(preferences).findByUserIdAndTermOrderByRankOrderAsc(42L,"Term A");
    }
    @Test void savesRankedApprovedProjects() {
        when(users.findByUsername(username)).thenReturn(Optional.of(student));
        when(projects.findByIdAndStatusAndTerm(20L,"APPROVED","Term A")).thenReturn(Optional.of(second));
        when(projects.findByIdAndStatusAndTerm(10L,"APPROVED","Term A")).thenReturn(Optional.of(first));
        var result = service.submit(username,"Term A",List.of(20L,10L));
        assertEquals(List.of(20L,10L),result.stream().map(p -> p.projectId()).toList());
        assertEquals(1,result.get(0).rank()); assertEquals(2,result.get(1).rank());
        verify(preferences).deleteByUserIdAndTerm(42L,"Term A");
        verify(preferences).flush();
        verify(preferences).saveAll(anyList());
    }
    @Test void rejectsDuplicateProjectsWithoutDeletingSavedSelection() {
        when(users.findByUsername(username)).thenReturn(Optional.of(student));
        assertThrows(ResponseStatusException.class, () -> service.submit(username,"Term A",List.of(10L,10L)));
        verifyNoInteractions(preferences,projects);
    }
    @Test void rejectsEmptyRankings() {
        when(users.findByUsername(username)).thenReturn(Optional.of(student));
        assertThrows(ResponseStatusException.class, () -> service.submit(username,"Term A",List.of()));
        verifyNoInteractions(preferences,projects);
    }
    @Test void rejectsUnapprovedProjectWithoutDeletingSavedSelection() {
        when(users.findByUsername(username)).thenReturn(Optional.of(student));
        when(projects.findByIdAndStatusAndTerm(10L,"APPROVED","Term A")).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.submit(username,"Term A",List.of(10L)));
        verifyNoInteractions(preferences);
    }
    @Test void rejectsFacultyTryingToSubmit() {
        student.setRole("FACULTY");
        when(users.findByUsername(username)).thenReturn(Optional.of(student));
        assertThrows(ResponseStatusException.class, () -> service.submit(username,"Term A",List.of(10L)));
        verifyNoInteractions(preferences,projects);
    }
}
