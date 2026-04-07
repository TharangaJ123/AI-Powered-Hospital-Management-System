package com.sliit.user_management.config;

import com.sliit.user_management.model.Admin;
import com.sliit.user_management.model.Role;
import com.sliit.user_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@omnihealth.com").isEmpty()) {
            Admin admin = Admin.builder()
                    .email("admin@omnihealth.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .firstName("System")
                    .lastName("Admin")
                    .active(true)
                    .isVerified(true)
                    .build();

            userRepository.save(admin);
            System.out.println("Default Admin account created: admin@omnihealth.com / admin123");
        }
    }
}
