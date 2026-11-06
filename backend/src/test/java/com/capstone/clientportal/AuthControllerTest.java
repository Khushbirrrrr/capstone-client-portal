package com.capstone.clientportal;

import com.capstone.clientportal.controller.AuthController;
import com.capstone.clientportal.entity.AppUser;
import com.capstone.clientportal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Uses dynamically generated fake credentials, never real Sheridan or demo credentials. */
class AuthControllerTest {
    private UserRepository users;
    private PasswordEncoder encoder;
    private MockMvc mvc;
    private String username;
    private String secret;

    @BeforeEach
    void setup() {
        users = mock(UserRepository.class);
        encoder = mock(PasswordEncoder.class);
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(users, encoder)).build();
        username = "test-" + UUID.randomUUID().toString().substring(0, 8);
        secret = UUID.randomUUID().toString();
        SecurityContextHolder.clearContext();
    }
    private AppUser user(String role) {
        AppUser account = new AppUser();
        account.setUsername(username);
        account.setRole(role);
        account.setPasswordHash("hash-" + UUID.randomUUID());
        return account;
    }
    private String payload(String name, String password) {
        return "{\"username\":\"" + name + "\",\"password\":\"" + password + "\"}";
    }

    @Test void studentCanLoginUsingPortalUsername() throws Exception {
        AppUser account = user("STUDENT");
        when(users.findByUsername(username)).thenReturn(Optional.of(account));
        when(encoder.matches(secret, account.getPasswordHash())).thenReturn(true);
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(payload(username.toUpperCase(), secret)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("STUDENT"))
            .andExpect(jsonPath("$.username").value(username));
    }
    @Test void facultyCanLogin() throws Exception {
        AppUser account = user("FACULTY");
        when(users.findByUsername(username)).thenReturn(Optional.of(account));
        when(encoder.matches(secret, account.getPasswordHash())).thenReturn(true);
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(payload(username, secret)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("FACULTY"));
    }
    @Test void incorrectPasswordIsRejected() throws Exception {
        AppUser account = user("STUDENT");
        when(users.findByUsername(username)).thenReturn(Optional.of(account));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(payload(username, secret)))
            .andExpect(status().isUnauthorized());
    }
    @Test void unknownPortalUserIsRejected() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(payload(username, secret)))
            .andExpect(status().isUnauthorized());
    }
    @Test void emailAddressIsNotAUsername() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(payload("example@company.invalid", secret)))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(users);
    }
    @Test void missingPasswordIsRejected() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(payload(username, "")))
            .andExpect(status().isBadRequest());
    }
    @Test void inactiveAccountIsRejected() throws Exception {
        AppUser account = user("STUDENT"); account.setActive(false);
        when(users.findByUsername(username)).thenReturn(Optional.of(account));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(payload(username, secret)))
            .andExpect(status().isUnauthorized());
    }
}
