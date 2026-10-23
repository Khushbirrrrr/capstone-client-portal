package com.capstone.clientportal.config;

import com.capstone.clientportal.entity.AppUser;
import com.capstone.clientportal.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.findByEmailIgnoreCase(
                    "student@sheridancollege.ca").isEmpty()) {

                AppUser student = new AppUser();
                student.setEmail("student@sheridancollege.ca");
                student.setPasswordHash(
                    passwordEncoder.encode("Student123!")
                );
                student.setRole("STUDENT");
                student.setActive(true);

                userRepository.save(student);
            }

            if (userRepository.findByEmailIgnoreCase(
                    "faculty@sheridancollege.ca").isEmpty()) {

                AppUser faculty = new AppUser();
                faculty.setEmail("faculty@sheridancollege.ca");
                faculty.setPasswordHash(
                    passwordEncoder.encode("Faculty123!")
                );
                faculty.setRole("FACULTY");
                faculty.setActive(true);

                userRepository.save(faculty);
            }
        };
    }
}