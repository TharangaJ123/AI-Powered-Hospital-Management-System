package com.sliit.user_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    private Long doctorId; // simple ID reference
    private String doctorName;

    @Column(columnDefinition = "TEXT")
    private String medication;

    private String dosage;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Builder.Default
    private LocalDateTime prescribedAt = LocalDateTime.now();
}
