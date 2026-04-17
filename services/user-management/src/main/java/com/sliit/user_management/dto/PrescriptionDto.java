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
public class PrescriptionDto {
    // Unique identifier for the prescription
    private Long id;
    // ID of the patient for whom the medication is prescribed
    private Long patientId;
    // ID of the doctor who issued the prescription
    private Long doctorId;
    // The name of the prescribed medication
    private String medication;
    // The specific dosage amount and frequency (e.g., 500mg, twice a day)
    private String dosage;
    // Additional usage instructions for the patient
    private String instructions;
    // The date and time when the prescription was issued
    private LocalDateTime prescribedDate;
}
