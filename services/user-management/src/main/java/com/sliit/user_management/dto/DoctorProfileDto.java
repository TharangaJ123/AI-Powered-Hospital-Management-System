package com.sliit.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorProfileDto {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String specialization;
    private String name; // Concatenated name for easier consumption
    private String phoneNumber;
    private String licenseNumber;
    private int experienceYears;
    private boolean isVerified;
    private Double consultationFee;

    private Double averageRating;
    private Integer reviewCount;
}
