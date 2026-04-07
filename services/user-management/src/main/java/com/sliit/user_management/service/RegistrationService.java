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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebClient.Builder webClientBuilder;

    @Transactional
    public UserResponseDto registerByRole(UserRegistrationDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }
        Role role = request.getRole() == null ? Role.PATIENT : request.getRole();

        if (role == Role.ADMIN) {
            throw new RuntimeException("Direct Admin registration is not allowed. Please contact the system administrator.");
        }

        if (role == Role.DOCTOR && request.getDoctorRegistrationNumber() != null) {
            if (userRepository.findByDoctorRegistrationNumber(request.getDoctorRegistrationNumber()).isPresent()) {
                throw new RuntimeException("Doctor Registration Number already in use");
            }
        }

        User user;
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

        // Database එකට user ව save කිරීම
        user = userRepository.save(user);

        // Doctor කෙනෙක් නම්, Profile sync එක safely කරමු
        if (role == Role.DOCTOR) {
            try {
                syncDoctorProfile(user, "PENDING_APPROVAL");
            } catch (Exception e) {
                // සේවා ක්‍රෝධ වුණත් ලියාපදිංචිය සාර්ථකයි
                System.err.println("CRITICAL NOTE: User persisted but doctor profile sync failed: " + e.getMessage());
            }
        }

        return mapToResponseDto(user);
    }

    private UserResponseDto mapToResponseDto(User user) {
        UserResponseDto.UserResponseDtoBuilder builder = UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .firstName(user.getFirstName())
                .lastName(user.getLastName());

        if (user instanceof com.sliit.user_management.model.Doctor) {
            com.sliit.user_management.model.Doctor doctor = (com.sliit.user_management.model.Doctor) user;
            builder.doctorRegistrationNumber(doctor.getDoctorRegistrationNumber());
            builder.specialization(doctor.getSpecialization());
        }

        return builder.build();
    }

    private void syncDoctorProfile(User user, String status) {
        WebClient webClient = webClientBuilder.baseUrl("http://localhost:8085").build();

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
