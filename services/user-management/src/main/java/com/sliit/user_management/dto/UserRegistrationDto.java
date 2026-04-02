package com.sliit.user_management.dto;

import com.sliit.user_management.model.Role;
import lombok.Data;

@Data
public class UserRegistrationDto {
    private String email;
    private String password;
    private Role role;

    // Optional initial profile fields for PATIENT
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String dateOfBirth;
    private String specialization;
}
