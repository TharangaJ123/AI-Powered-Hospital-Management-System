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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

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
                .specialization(role == Role.DOCTOR ? request.getSpecialization() : null)
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
        } else if (role == Role.DOCTOR) {
            try {
                syncDoctorProfile(user, "PENDING_APPROVAL");
            } catch (Exception e) {
                System.err.println("Failed to create doctor profile during registration: " + e.getMessage());
            }
        }

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

    private void syncDoctorProfile(User user, String status) {
        WebClient webClient = webClientBuilder.baseUrl("http://localhost:8085").build();

        Map<String, Object> profileData = new java.util.HashMap<>();
        profileData.put("userId", user.getId());
        profileData.put("firstName", user.getFirstName());
        profileData.put("lastName", user.getLastName());
        profileData.put("email", user.getEmail());
        profileData.put("specialization", user.getSpecialization());
        profileData.put("licenseNumber", user.getDoctorRegistrationNumber());
        profileData.put("status", status);

        try {
            Map<String, Object> existingProfile = webClient.get()
                    .uri("/api/doctors/profiles/user/{userId}", user.getId())
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

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
            // Profile does not exist yet, so create it below.
        }

        webClient.post()
                .uri("/api/doctors/profiles")
                .bodyValue(profileData)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}
