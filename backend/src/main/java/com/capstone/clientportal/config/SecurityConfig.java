
package com.capstone.clientportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // Temporary configuration for local Sprint 1 demo
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Public endpoints
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/login"
                ).permitAll()

                .requestMatchers(
                    "/api/health"
                ).permitAll()

                // Student-only project catalogue
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/projects",
                    "/api/projects/**"
                ).access((authentication, context) -> {

                    var authResult = authentication.get();

                    if (authResult == null
                            || !authResult.isAuthenticated()
                            || authResult instanceof
                               org.springframework.security.authentication
                                   .AnonymousAuthenticationToken) {

                        return new org.springframework.security
                            .authorization.AuthorizationDecision(false);
                    }

                    boolean isStudent = authResult
                        .getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                            authority.getAuthority()
                                .equals("ROLE_STUDENT")
                        );

                    return new org.springframework.security
                        .authorization.AuthorizationDecision(isStudent);
                })

                // All remaining endpoints require authentication
                .anyRequest().authenticated()
            )

            // Return 401 for unauthenticated requests
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                    new HttpStatusEntryPoint(
                        HttpStatus.UNAUTHORIZED
                    )
                )
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

