package com.capstone.clientportal.config;

import com.capstone.clientportal.entity.AppUser;
import com.capstone.clientportal.entity.Project;
import com.capstone.clientportal.entity.Term;
import com.capstone.clientportal.repository.ProjectRepository;
import com.capstone.clientportal.repository.TermRepository;
import com.capstone.clientportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Locale;

@Configuration
public class DataInitializer {
    /** Backfill Sprint 1 accounts without requiring a destructive database reset.
     * Existing hashes are NOT replaced without explicit demo-password configuration. */
    @Bean
    CommandLineRunner initialize(UserRepository users, ProjectRepository projects, TermRepository terms,
            PasswordEncoder encoder, @Value("${portal.demo.seed:false}") boolean seed,
            @Value("${portal.demo.student-password:}") String studentPassword,
            @Value("${portal.demo.faculty-password:}") String facultyPassword,
            @Value("${portal.demo.student-username:}") String studentUsername,
            @Value("${portal.demo.faculty-username:}") String facultyUsername,
            @Value("${portal.default-term:Fall 2026}") String defaultTerm) {
        return args -> {
            for (AppUser user : users.findAll()) {
                if (user.getUsername() == null || user.getUsername().isBlank()) {
                    // Prior Sprint 1 accounts used hardcoded passwords. Disable them on migration.
                    // New demo accounts are provisioned only with locally supplied credentials.
                    user.setUsername("migrated-user-" + user.getId());
                    user.setActive(false);
                }
                // Drop stored legacy addresses regardless of their former domain/format.
                // The legacy column is only used for compatibility with Sprint 1 PostgreSQL.
                if (user.getEmail() == null || !user.getEmail().endsWith("@portal.invalid")) {
                    user.setEmail("legacy-" + user.getId() + "@portal.invalid");
                }
                users.save(user);
            }
            if (!seed) return;
            // No secrets in source: demo accounts can only be enabled with local environment passwords.
            if (studentPassword.isBlank() || facultyPassword.isBlank()
                    || studentUsername.isBlank() || facultyUsername.isBlank()) {
                throw new IllegalStateException("Demo seeding requires all four PORTAL_DEMO_* credential variables");
            }
            if (studentPassword.length() < 12 || facultyPassword.length() < 12
                    || studentPassword.startsWith("CHOOSE_") || facultyPassword.startsWith("CHOOSE_")
                    || studentPassword.equals(facultyPassword)) {
                throw new IllegalStateException("Use different custom demo passwords of at least 12 characters");
            }
            String studentName = validUsername(studentUsername);
            String facultyName = validUsername(facultyUsername);
            if (studentName.equals(facultyName) || facultyName.equals("student_demo2") || studentName.equals("student_demo2")) throw new IllegalStateException("Demo usernames must differ");
            demoUser(users, encoder, studentName, "STUDENT", studentPassword);
            demoUser(users, encoder, "student_demo2", "STUDENT", studentPassword);
            demoUser(users, encoder, facultyName, "FACULTY", facultyPassword);
            terms.findByName(defaultTerm).orElseGet(() -> terms.save(new Term(defaultTerm, true)));
            if (projects.findByStatusAndTerm("APPROVED", defaultTerm).isEmpty()) {
                projects.save(new Project("Community Events Portal", "Build a community event scheduling site.", "APPROVED", defaultTerm));
                projects.save(new Project("Campus Sustainability Dashboard", "Visualize campus sustainability measures and opportunities.", "APPROVED", defaultTerm));
                projects.save(new Project("Volunteer Coordination App", "Organize volunteer opportunities and participation.", "APPROVED", defaultTerm));
                projects.save(new Project("Local Business Booking System", "Help clients book appointments with small businesses.", "APPROVED", defaultTerm));
            }
        };
    }
    private String validUsername(String input) {
        String value = input.trim().toLowerCase(Locale.ROOT);
        if (!value.matches("[a-z][a-z0-9._-]{2,39}"))
            throw new IllegalStateException("Demo usernames must be 3–40 letters, digits, dots, underscores or hyphens");
        return value;
    }
    private void demoUser(UserRepository users, PasswordEncoder encoder, String username, String role, String password) {
        AppUser user = users.findByUsername(username).orElseGet(AppUser::new);
        user.setUsername(username);
        if (user.getEmail() == null) user.setEmail(username + "@portal.invalid");
        user.setRole(role);
        user.setActive(true);
        user.setPasswordHash(encoder.encode(password)); // Explicitly rotate historical demo credentials.
        users.save(user);
    }
}
