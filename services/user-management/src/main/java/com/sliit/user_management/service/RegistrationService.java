package com.sliit.user_management.service;

import com.sliit.user_management.dto.UserRegistrationDto;
import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.model.PatientProfile;
import com.sliit.user_management.model.Role;
import com.sliit.user_management.model.User;
import com.sliit.user_management.repository.PatientProfileRepository;
import com.sliit.user_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebClient.Builder webClientBuilder;

    @Transactional
    public UserResponseDto registerByRole(UserRegistrationDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }
        Role role = request.getRole() == null ? Role.PATIENT : request.getRole();

        if (role == Role.DOCTOR && request.getDoctorRegistrationNumber() != null) {
            if (userRepository.findByDoctorRegistrationNumber(request.getDoctorRegistrationNumber()).isPresent()) {
                throw new RuntimeException("Doctor Registration Number already in use");
            }
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .active(role != Role.DOCTOR)
                .isVerified(role != Role.DOCTOR)
                .doctorRegistrationNumber(role == Role.DOCTOR ? request.getDoctorRegistrationNumber() : null)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        user = userRepository.save(user);

        if (role == Role.PATIENT) {
            PatientProfile profile = PatientProfile.builder()
                    .user(user)
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phoneNumber(request.getPhoneNumber())
                    .address(request.getAddress())
                    .dateOfBirth(request.getDateOfBirth())
                    .build();
            patientProfileRepository.save(profile);
        }

        // Trigger asynchronous welcome email and SMS notification
        sendWelcomeEmail(user, request.getPhoneNumber());

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .doctorRegistrationNumber(user.getDoctorRegistrationNumber())
                .build();
    }

    private void sendWelcomeEmail(User user, String phoneNumber) {
        try {
            Map<String, Object> notification = new java.util.HashMap<>();
            notification.put("type", "USER_REGISTERED");
            notification.put("recipientName", user.getFirstName() + " " + user.getLastName());
            notification.put("recipientEmail", user.getEmail());
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                notification.put("recipientPhone", phoneNumber);
            }

            webClientBuilder.build()
                .post()
                .uri("http://notification-service/api/notifications/send")
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(5))
                .doOnSuccess(r -> log.info("Welcome email triggered for registered user: {}", user.getEmail()))
                .doOnError(e -> log.error("Failed to trigger welcome email for user: {}", user.getEmail(), e))
                .subscribe(); // Async execution
        } catch (Exception e) {
            log.error("Error setting up welcome email notification for {}", user.getEmail(), e);
        }
    }
}
