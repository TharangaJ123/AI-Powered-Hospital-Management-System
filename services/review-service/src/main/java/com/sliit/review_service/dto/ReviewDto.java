package com.sliit.review_service.dto;

import com.sliit.review_service.entity.ReviewStatus;
import lombok.Data;

@Data
public class ReviewDto {
    // ID of the doctor being reviewed
    private Long doctorId;
    // ID of the patient who submitted the review
    private Long patientId;
    // ID of the specific appointment associated with the review
    private Long appointmentId;
    // Numerical rating given by the patient (e.g., 1 to 5)
    private Integer rating;
    // Textual feedback or comments provided by the patient
    private String comment;
    // The current status of the review (e.g., PENDING, APPROVED)
    private ReviewStatus status;
}
