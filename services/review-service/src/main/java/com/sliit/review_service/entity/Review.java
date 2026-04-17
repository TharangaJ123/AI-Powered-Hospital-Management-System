package com.sliit.review_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    // Primary key for the review entity, automatically generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID of the doctor associated with this review
    @Column(nullable = false)
    private Long doctorId;

    // ID of the patient who wrote this review
    @Column(nullable = false)
    private Long patientId;

    // ID of the specific appointment this review refers to
    @Column(nullable = false)
    private Long appointmentId;

    // Rating given by the patient, typically on a scale of 1 to 5
    @Column(nullable = false)
    private Integer rating; // 1 to 5

    // Textual feedback provided by the patient, up to 2000 characters
    @Column(length = 2000)
    private String comment;

    // Current status of the review (e.g., PENDING, APPROVED) stored as a string
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status;

    // Timestamp when the review was created, not updatable
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Lifecycle hook to set the creation timestamp and default status before persisting
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ReviewStatus.PENDING; // or APPROVED if you want auto-approve
        }
    }
}
