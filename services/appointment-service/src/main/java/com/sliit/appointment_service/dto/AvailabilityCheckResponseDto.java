package com.sliit.appointment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityCheckResponseDto {
    // Boolean flag indicating if the doctor is free for the requested slot
    private boolean available;
    // Human-readable message explaining the availability status
    private String message;
}
