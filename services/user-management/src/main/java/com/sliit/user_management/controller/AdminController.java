package com.sliit.user_management.controller;

import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing administrative users and platform operations.
 * Provides endpoints for user management, doctor verification, and retrieving platform statistics.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    // Injecting AdminService to handle administrative business logic
    private final AdminService adminService;

    // Endpoint to retrieve a list of all registered users
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // Endpoint to enable or disable a specific user account
    @PutMapping("/users/{userId}/toggle")
    public ResponseEntity<UserResponseDto> toggleUserStatus(@PathVariable Long userId, @RequestParam boolean active) {
        return ResponseEntity.ok(adminService.toggleUserStatus(userId, active));
    }

    // Endpoint to verify a doctor's credentials and registration
    @PutMapping("/doctors/{userId}/verify")
    public ResponseEntity<UserResponseDto> verifyDoctor(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.verifyDoctor(userId));
    }

    // Endpoint to fetch general platform statistics and operational data
    @GetMapping("/operations")
    public ResponseEntity<Object> getPlatformOperations() {
        return ResponseEntity.ok(adminService.getPlatformOperations());
    }

    // Endpoint to permanently delete a user from the system
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint to retrieve all financial transactions recorded in the platform
    @GetMapping("/transactions")
    public ResponseEntity<List<com.sliit.user_management.model.FinancialTransaction>> getAllTransactions() {
        return ResponseEntity.ok(adminService.getAllTransactions());
    }

    // Endpoint to delete a specific review submitted by a patient
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        adminService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
