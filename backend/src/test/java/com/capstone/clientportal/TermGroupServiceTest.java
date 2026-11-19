package com.capstone.clientportal;

import com.capstone.clientportal.entity.AppUser;
import com.capstone.clientportal.entity.Term;
import com.capstone.clientportal.repository.GroupRepository;
import com.capstone.clientportal.repository.TermRepository;
import com.capstone.clientportal.repository.UserRepository;
import com.capstone.clientportal.service.TermGroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TermGroupServiceTest {
    @Mock TermRepository terms;
    @Mock GroupRepository groups;
    @Mock UserRepository users;
    @InjectMocks TermGroupService service;

    @Test void rejectsMoreThanFourMembersBeforeLookingUpUsers() {
        // Generate the values instead of relying on several specific test accounts.
        List<String> names = IntStream.rangeClosed(1, 5).mapToObj(i -> "student" + i).toList();
        when(terms.findById(1L)).thenReturn(Optional.of(new Term("Fall 2026", true)));
        assertThrows(ResponseStatusException.class,
                () -> service.createGroup("Group A", 1L, names));
        verifyNoInteractions(users);
        verify(groups, never()).save(any());
    }

    @Test void rejectsDuplicateUsernamesIgnoringCase() {
        when(terms.findById(1L)).thenReturn(Optional.of(new Term("Fall 2026", true)));
        AppUser first = new AppUser();
        first.setUsername("student1"); first.setRole("STUDENT"); first.setActive(true);
        when(users.findByUsername("student1")).thenReturn(Optional.of(first));
        assertThrows(ResponseStatusException.class,
                () -> service.createGroup("Group A", 1L, List.of("Student1", "student1")));
        verify(groups, never()).save(any());
    }

    @Test void rejectsBlankMemberUsername() {
        when(terms.findById(1L)).thenReturn(Optional.of(new Term("Fall 2026", true)));
        assertThrows(ResponseStatusException.class,
                () -> service.createGroup("Group A", 1L, List.of("  ")));
        verifyNoInteractions(users);
        verify(groups, never()).save(any());
    }

    @Test void rejectsEmptyMemberList() {
        when(terms.findById(1L)).thenReturn(Optional.of(new Term("Fall 2026", true)));
        assertThrows(ResponseStatusException.class,
                () -> service.createGroup("Group A", 1L, List.of()));
        verifyNoInteractions(users);
        verify(groups, never()).save(any());
    }
}
