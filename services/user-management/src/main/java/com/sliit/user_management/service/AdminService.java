package com.sliit.user_management.service;

import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.model.User;
import com.sliit.user_management.repository.FinancialTransactionRepository;
import com.sliit.user_management.repository.ReviewRepository;
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
    private final com.sliit.user_management.repository.FinancialTransactionRepository financialTransactionRepository;
    private final ReviewRepository reviewRepository;

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
