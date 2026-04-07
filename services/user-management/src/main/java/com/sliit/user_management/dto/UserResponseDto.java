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
    private Long id;
    private String email;
    private Role role;
    private boolean active;
    private String firstName;
    private String lastName;
    private String doctorRegistrationNumber;
    private String specialization;
}
