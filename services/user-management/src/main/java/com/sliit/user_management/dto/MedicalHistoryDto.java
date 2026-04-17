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
    // Unique identifier for the medical history record
    private Long id;
    // ID of the patient associated with this medical record
    private Long patientId;
    // The name of the medical condition or illness
    private String conditionName;
    // The formal diagnosis provided by a medical professional
    private String diagnosis;
    // The treatment or medication prescribed for the condition
    private String treatment;
    // The date and time when this medical record was entered
    private LocalDateTime recordedDate;
}
