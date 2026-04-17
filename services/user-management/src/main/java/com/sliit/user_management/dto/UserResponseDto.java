package com.sliit.user_management.dto;

import com.sliit.user_management.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    // The unique ID of the user
    private Long id;
    // The user's primary email address
    private String email;
    // The assigned role of the user (e.g., ADMIN, PATIENT)
    private Role role;
    // Flag indicating if the user account is active
    private boolean active;
    // User's first name
    private String firstName;
    // User's last name
    private String lastName;
    // Doctor's official registration or license number (if applicable)
    private String doctorRegistrationNumber;
    // Doctor's area of medical expertise (if applicable)
    private String specialization;
    // User's contact phone number (if applicable)
    private String phoneNumber;
}
