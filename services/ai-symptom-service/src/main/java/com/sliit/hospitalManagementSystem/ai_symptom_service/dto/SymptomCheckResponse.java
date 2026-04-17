package com.sliit.hospitalManagementSystem.ai_symptom_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SymptomCheckResponse {
    // The AI-generated preliminary diagnosis based on the provided symptoms
    private String diagnosis;
    // The identified medical or clinical condition
    private String clinicalCondition;
    // A list of suggested actions or lifestyle changes for the patient
    private List<String> recommendations;
    // A list of medical specialties (e.g., Cardiology) the patient should consult
    private List<String> recommendedSpecialties;
    // The level of medical urgency (e.g., LOW, MEDIUM, HIGH, EMERGENCY)
    private String urgencyLevel; // e.g., LOW, MEDIUM, HIGH, EMERGENCY
}
