package com.capstone.clientportal;

import com.capstone.clientportal.config.SecurityConfig;
import com.capstone.clientportal.controller.AdminController;
import com.capstone.clientportal.controller.PreferenceController;
import com.capstone.clientportal.service.PreferenceService;
import com.capstone.clientportal.service.TermGroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ AdminController.class, PreferenceController.class })
@Import(SecurityConfig.class)
class Sprint2SecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean PreferenceService preferences;
    @MockitoBean TermGroupService management;

    @Test void unauthenticatedCannotReadRankings() throws Exception {
        mvc.perform(get("/api/preferences").with(anonymous())).andExpect(status().isUnauthorized());
    }
    @Test void facultyCannotSeeStudentRankings() throws Exception {
        mvc.perform(get("/api/preferences").with(user("faculty").roles("FACULTY")))
                .andExpect(status().isForbidden());
    }
    @Test void studentCannotAdministerGroups() throws Exception {
        mvc.perform(get("/api/admin/terms").with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }
    @Test void facultyCanSeeTerms() throws Exception {
        when(management.terms()).thenReturn(List.of());
        mvc.perform(get("/api/admin/terms").with(user("faculty").roles("FACULTY")))
                .andExpect(status().isOk());
    }
    @Test void csrfIsRequiredForRankingUpdates() throws Exception {
        String body = "{\"term\":\"Term A\",\"projectIds\":[1]}";
        mvc.perform(put("/api/preferences").with(user("student").roles("STUDENT"))
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
        verifyNoInteractions(preferences);
    }
    @Test void validCsrfPermitsRankingUpdates() throws Exception {
        String body = "{\"term\":\"Term A\",\"projectIds\":[1]}";
        when(preferences.submit("student", "Term A", List.of(1L))).thenReturn(List.of());
        mvc.perform(put("/api/preferences").with(user("student").roles("STUDENT")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }
}
