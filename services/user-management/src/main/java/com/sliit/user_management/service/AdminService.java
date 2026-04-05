package com.sliit.user_management.service;

import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.model.User;
import com.sliit.user_management.repository.ReviewRepository;
import com.sliit.user_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling administrative operations.
 * This includes user management, doctor verification, and retrieving platform metrics.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final com.sliit.user_management.repository.FinancialTransactionRepository financialTransactionRepository;
    private final ReviewRepository reviewRepository;
    private final WebClient.Builder webClientBuilder;

    /**
     * Retrieves all registered users from the database.
     * 
     * @return a list of UserResponseDto representing all users
     */
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserResponseDto.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .active(u.isActive())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .doctorRegistrationNumber(u.getDoctorRegistrationNumber())
                        .specialization(u.getSpecialization())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Toggles the active status of a specified user.
     * 
     * @param userId the ID of the user to modify
     * @param isActive the new active status
     * @return the updated UserResponseDto
     * @throws RuntimeException if the user is not found
     */
    @Transactional
    public UserResponseDto toggleUserStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(isActive);
        user = userRepository.save(user);
        
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .specialization(user.getSpecialization())
                .build();
    }

    /**
     * Verifies a doctor user.
     * 
     * @param userId the ID of the user to verify
     * @return the updated UserResponseDto
     */
    @Transactional
    public UserResponseDto verifyDoctor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != com.sliit.user_management.model.Role.DOCTOR) {
            throw new RuntimeException("User is not a doctor");
        }
        
        user.setVerified(true);
        user.setActive(true);
        user = userRepository.save(user);

        // Synchronize with doctor-management service to create or activate the profile
        try {
            syncDoctorProfile(user, "ACTIVE");
        } catch (Exception e) {
            // Log error but don't fail verification
            System.err.println("Failed to sync doctor profile in doctor-management service: " + e.getMessage());
        }
        
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .doctorRegistrationNumber(user.getDoctorRegistrationNumber())
                .specialization(user.getSpecialization())
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
                    .bodyToMono(Map.class)
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



    /**
     * Retrieves overall platform statistics and operations data.
     * 
     * @return a JSON string representing platform statistics
     */
    public Object getPlatformOperations() {
        long totalUsers = userRepository.count();
        Double totalRevenue = financialTransactionRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = 0.0;
        
        long successfulTransactions = financialTransactionRepository.countByStatus("SUCCESS");
        
        return java.util.Map.of(
            "totalUsers", totalUsers,
            "totalRevenue", totalRevenue,
            "successfulTransactions", successfulTransactions
        );
    }

    /**
     * Deletes a user account and their associated profile.
     * 
     * @param userId the ID of the user to delete
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Profiles are linked via userId (physically or logically)
        // If they use @OneToOne with CascadeType.ALL, deleting user is enough.
        // Let's check User entity to be sure.
        userRepository.delete(user);
    }

    /**
     * Retrieves all financial transactions on the platform.
     * 
     * @return a list of FinancialTransaction entities (or DTOs)
     */
    public List<com.sliit.user_management.model.FinancialTransaction> getAllTransactions() {
        return financialTransactionRepository.findAllByOrderByTransactionDateDesc();
    }

    /**
     * Deletes a review by its ID.
     * 
     * @param reviewId the ID of the review to delete
     */
    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
