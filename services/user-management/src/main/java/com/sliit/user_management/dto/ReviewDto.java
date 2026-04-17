package com.sliit.user_management.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    // Unique identifier for the review
    private Long id;
    // ID of the doctor who is being reviewed
    private Long doctorId;
    // ID of the patient who wrote the review
    private Long patientId;
    // Numerical rating given by the patient (e.g., 1 to 5)
    private int rating;
    // Textual feedback or comments provided by the patient
    private String comment;
    // The timestamp when the review was submitted
    private LocalDateTime createdAt;
}
