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
public class MedicalHistoryDto {
    private Long id;
    private Long patientId;
    private String conditionName;
    private String diagnosis;
    private String treatment;
    private LocalDateTime recordedDate;
}
