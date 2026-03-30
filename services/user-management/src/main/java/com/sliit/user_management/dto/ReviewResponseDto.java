package com.sliit.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long id;
    private Long doctorId;
    private Long patientId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
