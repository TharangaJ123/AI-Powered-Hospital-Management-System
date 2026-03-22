package com.sliit.user_management.controller;

import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{userId}/toggle")
    public ResponseEntity<UserResponseDto> toggleUserStatus(@PathVariable Long userId, @RequestParam boolean active) {
        return ResponseEntity.ok(adminService.toggleUserStatus(userId, active));
    }

    @PutMapping("/doctors/{doctorId}/verify")
    public ResponseEntity<String> verifyDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(adminService.verifyDoctor(doctorId));
    }

    @GetMapping("/operations")
    public ResponseEntity<String> getPlatformOperations() {
        return ResponseEntity.ok(adminService.getPlatformOperations());
    }
}
