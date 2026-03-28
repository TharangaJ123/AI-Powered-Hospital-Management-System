package com.sliit.user_management.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
