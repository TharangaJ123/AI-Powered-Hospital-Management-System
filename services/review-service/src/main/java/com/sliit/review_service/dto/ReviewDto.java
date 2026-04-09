package com.sliit.review_service.dto;

import com.sliit.review_service.entity.ReviewStatus;
import lombok.Data;

@Data
public class ReviewDto {
    private Long doctorId;
    private Long patientId;
    private Long appointmentId;
    private Integer rating;
    private String comment;
    private ReviewStatus status;
}
