package com.sliit.user_management.dto;

import com.sliit.user_management.model.Role;
import lombok.Data;

@Data
public class UserRegistrationDto {
    // User's primary email address
    private String email;
    // User's secret password for the new account
    private String password;
    // The role being requested (e.g., PATIENT, DOCTOR)
    private Role role;

    // User's first name
    private String firstName;
    // User's last name
    private String lastName;
    // Contact phone number (primarily for patients)
    private String phoneNumber;
    // Residential address (primarily for patients)
    private String address;
    // Date of birth (primarily for patients)
    private String dateOfBirth;
    // Medical specialization (specifically for doctors)
    private String specialization;
    // Official registration or license number (specifically for doctors)
    private String doctorRegistrationNumber;
}
