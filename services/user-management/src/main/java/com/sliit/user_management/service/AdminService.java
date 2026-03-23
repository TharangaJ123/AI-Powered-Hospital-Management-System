package com.sliit.user_management.service;

import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.model.User;
import com.sliit.user_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .build();
    }

    /**
     * Verifies the credentials of a registered doctor.
     * Note: This method currently provides mock verification logic.
     * 
     * @param doctorId the ID of the doctor to verify
     * @return a success message string upon verification
     */
    public String verifyDoctor(Long doctorId) {
        // Mock logic for verifying doctor credentials
        return "Doctor Registration #" + doctorId + " verified successfully.";
    }

    /**
     * Retrieves overall platform statistics and operations data.
     * Note: This method currently provides mock data.
     * 
     * @return a JSON string representing platform statistics
     */
    public String getPlatformOperations() {
        // Mock platform stats
        return "{ \"activeUsers\": 150, \"monthlyRevenue\": \"$4500.50\", \"uptime\": \"99.9%\" }";
    }
}
