package com.sliit.user_management.service;

import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.model.User;
import com.sliit.user_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

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

    public String verifyDoctor(Long doctorId) {
        // Mock logic for verifying doctor credentials
        return "Doctor Registration #" + doctorId + " verified successfully.";
    }

    public String getPlatformOperations() {
        // Mock platform stats
        return "{ \"activeUsers\": 150, \"monthlyRevenue\": \"$4500.50\", \"uptime\": \"99.9%\" }";
    }
}
