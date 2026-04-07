package com.sliit.user_management.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private Long doctorId;
    private Long patientId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
