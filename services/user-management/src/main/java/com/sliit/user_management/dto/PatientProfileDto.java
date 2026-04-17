package com.sliit.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileDto {
    // Unique identifier for the patient profile
    private Long id;
    // ID of the user account associated with this profile
    private Long userId;
    // Patient's first name
    private String firstName;
    // Patient's last name
    private String lastName;
    // Patient's contact phone number
    private String phoneNumber;
    // Patient's residential address
    private String address;
    // Patient's date of birth in string format
    private String dateOfBirth;
    // Patient's primary email address
    private String email;
}
