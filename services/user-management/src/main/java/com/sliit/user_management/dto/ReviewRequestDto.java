package com.sliit.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {
    private Long doctorId;
    private int rating;
    private String comment;
}
