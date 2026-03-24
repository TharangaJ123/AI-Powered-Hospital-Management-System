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

    private final AdminService adminService;

    /**
     * Retrieves a list of all users registered in the system.
     * 
     * @return a ResponseEntity containing a list of UserResponseDto objects representing all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Toggles the active status of a user.
     * 
     * @param userId the ID of the user to update
     * @param active the new active status
     * @return a ResponseEntity containing the updated UserResponseDto
     */
    @PutMapping("/users/{userId}/toggle")
    public ResponseEntity<UserResponseDto> toggleUserStatus(@PathVariable Long userId, @RequestParam boolean active) {
        return ResponseEntity.ok(adminService.toggleUserStatus(userId, active));
    }

    /**
     * Verifies the credentials and registration of a doctor.
     * 
     * @param doctorId the ID of the doctor to verify
     * @return a ResponseEntity containing a success message string
     */
    @PutMapping("/doctors/{doctorId}/verify")
    public ResponseEntity<String> verifyDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(adminService.verifyDoctor(doctorId));
    }

    /**
     * Retrieves aggregated platform operations and statistics.
     * 
     * @return a ResponseEntity containing platform operations data in JSON string format
     */
    @GetMapping("/operations")
    public ResponseEntity<Object> getPlatformOperations() {
        return ResponseEntity.ok(adminService.getPlatformOperations());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<com.sliit.user_management.model.FinancialTransaction>> getAllTransactions() {
        return ResponseEntity.ok(adminService.getAllTransactions());
    }
}
