package com.sliit.review_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStatsDto {
    private Long doctorId;
    private Double averageRating;
    private Long totalReviews;
}
