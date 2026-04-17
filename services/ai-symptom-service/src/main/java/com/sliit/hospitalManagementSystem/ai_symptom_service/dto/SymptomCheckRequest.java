package com.sliit.hospitalManagementSystem.ai_symptom_service.dto;

import lombok.Data;

@Data
public class SymptomCheckRequest {
    // Description of the symptoms reported by the patient
    private String symptoms;
    // Age of the patient to provide context for medical analysis
    private int age;
    // Gender of the patient, which can influence certain diagnoses
    private String gender;
    // Relevant past or current medical conditions of the patient
    private String medicalHistory; // any hidden or current diseases
}
