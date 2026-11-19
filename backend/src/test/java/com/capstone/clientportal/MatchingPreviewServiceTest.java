package com.capstone.clientportal;

import com.capstone.clientportal.entity.AppUser;
import com.capstone.clientportal.entity.Project;
import com.capstone.clientportal.entity.RankedPreference;
import com.capstone.clientportal.entity.StudentGroup;
import com.capstone.clientportal.entity.Term;
import com.capstone.clientportal.repository.GroupRepository;
import com.capstone.clientportal.repository.PreferenceRepository;
import com.capstone.clientportal.repository.ProjectRepository;
import com.capstone.clientportal.repository.TermRepository;
import com.capstone.clientportal.service.MatchingPreviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingPreviewServiceTest {
    @Mock TermRepository terms;
    @Mock GroupRepository groups;
    @Mock ProjectRepository projects;
    @Mock PreferenceRepository preferences;
    @InjectMocks MatchingPreviewService service;

    @Test void recommendsProjectOnceAndReportsUnresolvedGroup() {
        Term term = new Term("Fall 2026", true);
        when(terms.findById(1L)).thenReturn(Optional.of(term));
        Project project = new Project("Project A", "Demo", "APPROVED", "Fall 2026");
        ReflectionTestUtils.setField(project, "id", 30L);
        when(projects.findByStatusAndTerm("APPROVED", "Fall 2026")).thenReturn(List.of(project));

        AppUser first = new AppUser(); first.setId(11L); first.setRole("STUDENT"); first.setActive(true);
        AppUser second = new AppUser(); second.setId(12L); second.setRole("STUDENT"); second.setActive(true);
        StudentGroup group1 = new StudentGroup(); group1.setName("Group 1"); group1.setMembers(List.of(first));
        StudentGroup group2 = new StudentGroup(); group2.setName("Group 2"); group2.setMembers(List.of(second));
        ReflectionTestUtils.setField(group1, "id", 1L);
        ReflectionTestUtils.setField(group2, "id", 2L);
        when(groups.findByTermId(1L)).thenReturn(List.of(group2, group1));
        when(preferences.findByUserIdAndTermOrderByRankOrderAsc(11L, "Fall 2026"))
                .thenReturn(List.of(new RankedPreference(first, project, "Fall 2026", 1, Instant.now())));
        when(preferences.findByUserIdAndTermOrderByRankOrderAsc(12L, "Fall 2026"))
                .thenReturn(List.of(new RankedPreference(second, project, "Fall 2026", 2, Instant.now())));

        var result = service.generate(1L);
        assertEquals(2, result.recommendations().size());
        assertEquals("RECOMMENDED", result.recommendations().get(0).status());
        assertEquals(30L, result.recommendations().get(0).projectId());
        assertEquals(5, result.recommendations().get(0).score());
        assertEquals("UNRESOLVED", result.recommendations().get(1).status());
        assertNull(result.recommendations().get(1).projectId());
        verify(groups).findByTermId(1L);
        verify(projects).findByStatusAndTerm("APPROVED", "Fall 2026");
    }

    @Test void emptyGroupsReturnEmptyPreview() {
        when(terms.findById(1L)).thenReturn(Optional.of(new Term("Fall 2026", true)));
        when(projects.findByStatusAndTerm("APPROVED", "Fall 2026")).thenReturn(List.of());
        when(groups.findByTermId(1L)).thenReturn(List.of());
        assertTrue(service.generate(1L).recommendations().isEmpty());
        verifyNoInteractions(preferences);
    }
}
