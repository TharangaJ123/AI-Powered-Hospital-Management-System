package com.sliit.user_management.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String firstName;
    private String lastName;
    private String specialization;
    private String phoneNumber;
    private String licenseNumber;
    private int experienceYears;
    
    @Builder.Default
    private boolean isVerified = false;

    private Double consultationFee;
}
