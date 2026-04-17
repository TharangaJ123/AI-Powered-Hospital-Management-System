package com.sliit.user_management.service;

import com.sliit.user_management.dto.UserRegistrationDto;
import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.model.Role;
import com.sliit.user_management.model.User;
import com.sliit.user_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    // Repository for user database operations
    private final UserRepository userRepository;
    // Encoder for securely hashing passwords
    private final PasswordEncoder passwordEncoder;
    // Builder for WebClient to make inter-service calls
    private final WebClient.Builder webClientBuilder;

    // Base URL for the Doctor Management microservice
    @org.springframework.beans.factory.annotation.Value("${doctor.management.url:http://localhost:8082}")
    private String doctorManagementUrl;

    // Base URL for the Notification microservice
    @org.springframework.beans.factory.annotation.Value("${notification.service.url:http://localhost:8085}")
    private String notificationServiceUrl;

    // Handles user registration based on their assigned role (Patient, Doctor, etc.)
    @Transactional
    public UserResponseDto registerByRole(UserRegistrationDto request) {
        // Prevent registration if the email is already in use
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }
        Role role = request.getRole() == null ? Role.PATIENT : request.getRole();

        // Block direct registration for Admin accounts for security
        if (role == Role.ADMIN) {
            throw new RuntimeException(
                    "Direct Admin registration is not allowed. Please contact the system administrator.");
        }

        // Validate doctor registration numbers to ensure uniqueness
        if (role == Role.DOCTOR && request.getDoctorRegistrationNumber() != null) {
            if (userRepository.findByDoctorRegistrationNumber(request.getDoctorRegistrationNumber()).isPresent()) {
                throw new RuntimeException("Doctor Registration Number already in use");
            }
        }

        User user;
        // Build the appropriate user entity based on the role
        switch (role) {
            case DOCTOR:
                user = com.sliit.user_management.model.Doctor.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.DOCTOR)
                        .active(false) // Doctors need approval
                        .isVerified(false)
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .doctorRegistrationNumber(request.getDoctorRegistrationNumber())
                        .specialization(request.getSpecialization())
                        .build();
                break;
            case ADMIN:
                user = com.sliit.user_management.model.Admin.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.ADMIN)
                        .active(true)
                        .isVerified(true)
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .build();
                break;
            case PATIENT:
            default:
                user = com.sliit.user_management.model.Patient.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.PATIENT)
                        .active(true)
                        .isVerified(true)
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .phoneNumber(request.getPhoneNumber())
                        .address(request.getAddress())
                        .dateOfBirth(request.getDateOfBirth())
                        .build();
                break;
        }

        // Save the newly created user to the database
        user = userRepository.save(user);

        // Trigger welcome notifications asynchronously
        sendRegistrationNotification(user);

        // Sync data with the Doctor Management service if the user is a doctor
        if (role == Role.DOCTOR) {
            try {
                syncDoctorProfile(user, "PENDING_APPROVAL");
            } catch (Exception e) {
                // Log failure but allow registration to complete
                System.err.println("CRITICAL NOTE: User persisted but doctor profile sync failed: " + e.getMessage());
            }
        }

        return mapToResponseDto(user);
    }

    // Converts a User entity into a UserResponseDto for the client
    private UserResponseDto mapToResponseDto(User user) {
        UserResponseDto.UserResponseDtoBuilder builder = UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .firstName(user.getFirstName())
                .lastName(user.getLastName());

        // Include role-specific fields in the DTO
        if (user instanceof com.sliit.user_management.model.Doctor) {
            com.sliit.user_management.model.Doctor doctor = (com.sliit.user_management.model.Doctor) user;
            builder.doctorRegistrationNumber(doctor.getDoctorRegistrationNumber());
            builder.specialization(doctor.getSpecialization());
        } else if (user instanceof com.sliit.user_management.model.Patient) {
            com.sliit.user_management.model.Patient patient = (com.sliit.user_management.model.Patient) user;
            builder.phoneNumber(patient.getPhoneNumber());
        }

        return builder.build();
    }

    // Synchronizes doctor information with the Doctor Management microservice
    private void syncDoctorProfile(User user, String status) {
        WebClient webClient = webClientBuilder.baseUrl(doctorManagementUrl).build();

        Map<String, Object> profileData = new java.util.HashMap<>();
        profileData.put("userId", user.getId());
        profileData.put("firstName", user.getFirstName());
        profileData.put("lastName", user.getLastName());
        profileData.put("email", user.getEmail());

        if (user instanceof com.sliit.user_management.model.Doctor) {
            com.sliit.user_management.model.Doctor doctor = (com.sliit.user_management.model.Doctor) user;
            profileData.put("specialization", doctor.getSpecialization());
            profileData.put("licenseNumber", doctor.getDoctorRegistrationNumber());
        }

        profileData.put("status", status);

        try {
            // Check if a profile already exists for this user
            Map<String, Object> existingProfile = webClient.get()
                    .uri("/api/doctors/profiles/user/{userId}", user.getId())
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            // Update existing profile if found
            if (existingProfile != null && existingProfile.get("id") != null) {
                Number profileId = (Number) existingProfile.get("id");
                webClient.put()
                        .uri("/api/doctors/profiles/{id}", profileId.longValue())
                        .bodyValue(profileData)
                        .retrieve()
                        .bodyToMono(Object.class)
                        .block();
                return;
            }
        } catch (WebClientResponseException.NotFound ignored) {
            // Profile does not exist yet, will be created below
        }

        // Create a new doctor profile in the doctor management service
        webClient.post()
                .uri("/api/doctors/profiles")
                .bodyValue(profileData)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    // Sends registration success notifications via the Notification service
    private void sendRegistrationNotification(User user) {
        WebClient webClient = webClientBuilder.baseUrl(notificationServiceUrl).build();
        Map<String, Object> request = new java.util.HashMap<>();
        request.put("type", "USER_REGISTERED");
        request.put("recipientName", user.getFirstName() + " " + user.getLastName());
        request.put("recipientEmail", user.getEmail());

        if (user instanceof com.sliit.user_management.model.Patient) {
            request.put("recipientPhone", ((com.sliit.user_management.model.Patient) user).getPhoneNumber());
        }

        // POST the notification data to the notification service endpoint
        webClient.post()
                .uri("/api/notifications/send")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe(
                        success -> System.out
                                .println("Registration notification sent asynchronously for: " + user.getEmail()),
                        error -> System.err.println("Failed to send registration notification: " + error.getMessage()));
    }
}