package com.sliit.hospitalManagementSystem.doctor_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    // Unique identifier for the patient review record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key reference to the doctor being rated
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    // Foreign key reference to the patient providing the feedback
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    // Name of the patient recorded at the time of review
    @Column(name = "patient_name")
    private String patientName;

    // Numerical score provided by the patient (typically on a scale of 1-5)
    @Column(name = "rating", nullable = false)
    private int rating;

    // Written feedback or detailed comments regarding the doctor's service
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    // Timestamp indicating when the review was submitted
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // JPA hook to automatically set the creation timestamp before persistence
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
