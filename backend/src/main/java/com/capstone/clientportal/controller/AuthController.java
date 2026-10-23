
package com.capstone.clientportal.controller;

import com.capstone.clientportal.entity.AppUser;
import com.capstone.clientportal.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // US07 - Sign in using Sheridan credentials
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        if (request == null ||
                request.email() == null ||
                request.password() == null ||
                request.email().isBlank() ||
                request.password().isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", "Email and password are required")
            );
        }

        String email = request.email().trim().toLowerCase();

        if (!email.endsWith("@sheridancollege.ca")) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Use a Sheridan email address")
            );
        }

        Optional<AppUser> userOptional =
                userRepository.findByEmailIgnoreCase(email);

        if (userOptional.isEmpty()) {
            return invalidCredentials();
        }

        AppUser user = userOptional.get();

        if (!user.isActive() ||
                !passwordEncoder.matches(
                        request.password(),
                        user.getPasswordHash())) {

            return invalidCredentials();
        }

        // Create authenticated Spring Security identity
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + user.getRole()
                                )
                        )
                );

        // Save authentication in Spring Security context
        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Create session and protect against session fixation
        HttpSession existingSession = httpRequest.getSession(false);

        if (existingSession != null) {
            httpRequest.changeSessionId();
        }

        HttpSession session = httpRequest.getSession(true);

        // Persist authentication across requests
        HttpSessionSecurityContextRepository contextRepository =
                new HttpSessionSecurityContextRepository();

        contextRepository.saveContext(
                securityContext,
                httpRequest,
                httpResponse
        );

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful",
                        "email", user.getEmail(),
                        "role", user.getRole()
                )
        );
    }

    // US07 - Check currently authenticated user
    @GetMapping("/me")
    public ResponseEntity<?> currentUser(
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "Not authenticated"
                    ));
        }

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority -> authority.getAuthority()
                        .replaceFirst("^ROLE_", ""))
                .orElse("UNKNOWN");

        return ResponseEntity.ok(
                Map.of(
                        "email", authentication.getName(),
                        "role", role
                )
        );
    }

    // US07 - Logout and invalidate session
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                Map.of("message", "Logout successful")
        );
    }

    // Generic error avoids revealing whether an account exists
    private ResponseEntity<?> invalidCredentials() {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "message", "Invalid email or password"
                ));
    }

    public record LoginRequest(
            String email,
            String password
    ) {}
}
