
package com.capstone.clientportal;

import com.capstone.clientportal.controller.AuthController;
import com.capstone.clientportal.entity.AppUser;
import com.capstone.clientportal.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        AuthController controller =
                new AuthController(userRepository, passwordEncoder);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        SecurityContextHolder.clearContext();
    }

    private AppUser createUser(String email, String role) {
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPasswordHash("hashed-password");
        user.setRole(role);
        user.setActive(true);
        return user;
    }

    @Test
    void studentLoginShouldSucceed() throws Exception {
        AppUser student = createUser(
                "student@sheridancollege.ca", "STUDENT");

        when(userRepository.findByEmailIgnoreCase(anyString()))
                .thenReturn(Optional.of(student));

        when(passwordEncoder.matches(
                "Student123!", "hashed-password"))
                .thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "student@sheridancollege.ca",
                      "password": "Student123!"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.message")
                        .value("Login successful"));
    }

    @Test
    void facultyLoginShouldSucceed() throws Exception {
        AppUser faculty = createUser(
                "faculty@sheridancollege.ca", "FACULTY");

        when(userRepository.findByEmailIgnoreCase(anyString()))
                .thenReturn(Optional.of(faculty));

        when(passwordEncoder.matches(
                "Faculty123!", "hashed-password"))
                .thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "faculty@sheridancollege.ca",
                      "password": "Faculty123!"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("FACULTY"));
    }

    @Test
    void incorrectPasswordShouldReturn401() throws Exception {
        AppUser student = createUser(
                "student@sheridancollege.ca", "STUDENT");

        when(userRepository.findByEmailIgnoreCase(anyString()))
                .thenReturn(Optional.of(student));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "student@sheridancollege.ca",
                      "password": "WrongPassword"
                    }
                    """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password"));
    }

    @Test
    void unknownEmailShouldReturn401() throws Exception {
        when(userRepository.findByEmailIgnoreCase(anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "unknown@sheridancollege.ca",
                      "password": "Password123!"
                    }
                    """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void nonSheridanEmailShouldBeRejected() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "student@gmail.com",
                      "password": "Password123!"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Use a Sheridan email address"));

        verifyNoInteractions(userRepository);
    }

    @Test
    void missingPasswordShouldBeRejected() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "student@sheridancollege.ca",
                      "password": ""
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Email and password are required"));
    }

    @Test
    void inactiveAccountShouldReturn401() throws Exception {
        AppUser student = createUser(
                "student@sheridancollege.ca", "STUDENT");
        student.setActive(false);

        when(userRepository.findByEmailIgnoreCase(anyString()))
                .thenReturn(Optional.of(student));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "student@sheridancollege.ca",
                      "password": "Student123!"
                    }
                    """))
                .andExpect(status().isUnauthorized());
    }
}
