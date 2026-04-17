package com.sliit.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDto {
    // The generated JWT token for authentication
    private String token;
    // The unique ID of the authenticated user
    private Long id;
    // The email address of the user
    private String email;
    // The full name of the user
    private String name;
    // The security role assigned to the user
    private String role;
}
