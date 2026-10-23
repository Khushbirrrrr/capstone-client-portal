
package com.capstone.clientportal;

import com.capstone.clientportal.controller.ProjectController;
import com.capstone.clientportal.service.ProjectService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import com.capstone.clientportal.config.SecurityConfig;
import org.springframework.context.annotation.Import;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;

@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)class ProjectSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Test
void anonymousUserCannotBrowseProjects() throws Exception {
    mockMvc.perform(get("/api/projects")
            .with(anonymous()))
        .andExpect(status().isUnauthorized());
}

    @Test
    void studentCanBrowseApprovedProjects() throws Exception {
        when(projectService.getApprovedProjects(eq("Fall 2026")))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/projects")
                .with(user("student@sheridancollege.ca")
                .roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void facultyCannotUseStudentOnlyEndpoint() throws Exception {
        mockMvc.perform(get("/api/projects")
                .with(user("faculty@sheridancollege.ca")
                .roles("FACULTY")))
                .andExpect(status().isForbidden());
    }
}
