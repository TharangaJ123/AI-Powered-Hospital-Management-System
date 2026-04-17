package com.sliit.user_management.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    // User's email address used for identification during login
    private String email;
    // User's secret password for authentication
    private String password;
}
