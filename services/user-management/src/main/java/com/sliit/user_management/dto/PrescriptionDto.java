package com.sliit.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionDto {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String doctorName;
    private String medication;
    private String dosage;
    private String instructions;
    private LocalDateTime prescribedAt;
}
