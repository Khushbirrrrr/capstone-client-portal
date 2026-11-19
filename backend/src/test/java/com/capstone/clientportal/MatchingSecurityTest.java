package com.capstone.clientportal;

import com.capstone.clientportal.config.SecurityConfig;
import com.capstone.clientportal.controller.MatchingController;
import com.capstone.clientportal.service.MatchingPreviewService;
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

@WebMvcTest(MatchingController.class)
@Import(SecurityConfig.class)
class MatchingSecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean MatchingPreviewService service;
    private static final String BODY = "{\"termId\":1}";

    @Test void studentCannotGeneratePreview() throws Exception {
        mvc.perform(post("/api/matching/generate").with(user("student").roles("STUDENT")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }

    @Test void facultyCanGeneratePreviewWithCsrf() throws Exception {
        when(service.generate(1L)).thenReturn(
                new MatchingPreviewService.MatchingPreview(1L, "Fall 2026", List.of()));
        mvc.perform(post("/api/matching/generate").with(user("faculty").roles("FACULTY")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.term").value("Fall 2026"));
    }

    @Test void requestWithoutCsrfIsDenied() throws Exception {
        mvc.perform(post("/api/matching/generate").with(user("faculty").roles("FACULTY"))
                .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }
}
