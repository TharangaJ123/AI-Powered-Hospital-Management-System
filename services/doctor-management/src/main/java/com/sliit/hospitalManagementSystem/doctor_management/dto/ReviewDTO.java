package com.sliit.hospitalManagementSystem.doctor_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    // Unique identifier for the review record
    private Long id;
    // ID of the doctor being reviewed
    private Long doctorId;
    // ID of the patient providing the feedback
    private Long patientId;
    // Name of the patient (cached for display)
    private String patientName;
    // Numeric rating given by the patient (e.g., 1 to 5)
    private int rating;
    // Detailed text feedback or comments from the patient
    private String comment;
    // Timestamp when the review was submitted
    private LocalDateTime createdAt;
}
