package com.sliit.review_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStatsDto {
    // The unique identifier for the doctor
    private Long doctorId;
    // The calculated average rating for the doctor based on all reviews
    private Double averageRating;
    // The total count of reviews submitted for this doctor
    private Long totalReviews;
}
