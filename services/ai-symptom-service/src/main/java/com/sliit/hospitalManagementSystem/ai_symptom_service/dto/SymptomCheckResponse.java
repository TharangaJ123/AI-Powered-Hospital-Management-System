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
    private String diagnosis;
    private String recommendations;
    private List<String> recommendedSpecialties;
    private String urgencyLevel; // e.g., LOW, MEDIUM, HIGH, EMERGENCY
}
