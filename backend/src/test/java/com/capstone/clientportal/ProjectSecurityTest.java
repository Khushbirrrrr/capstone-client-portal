package com.capstone.clientportal;

import com.capstone.clientportal.config.SecurityConfig;
import com.capstone.clientportal.controller.ProjectController;
import com.capstone.clientportal.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)
class ProjectSecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean ProjectService projects;

    @Test void anonymousCannotBrowse() throws Exception {
        mvc.perform(get("/api/projects").with(anonymous())).andExpect(status().isUnauthorized());
    }
    @Test void studentCanBrowse() throws Exception {
        when(projects.getApprovedProjects(anyString())).thenReturn(List.of());
        mvc.perform(get("/api/projects").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }
    @Test void facultyCannotBrowseStudentOnlyProjects() throws Exception {
        mvc.perform(get("/api/projects").with(user("faculty").roles("FACULTY")))
                .andExpect(status().isForbidden());
    }
    @Test void facultyCannotFetchStudentProjectDetail() throws Exception {
        mvc.perform(get("/api/projects/1").with(user("faculty").roles("FACULTY")))
                .andExpect(status().isForbidden());
    }
    @Test void anonymousCannotFetchStudentProjectDetail() throws Exception {
        mvc.perform(get("/api/projects/1").with(anonymous())).andExpect(status().isUnauthorized());
    }
}
