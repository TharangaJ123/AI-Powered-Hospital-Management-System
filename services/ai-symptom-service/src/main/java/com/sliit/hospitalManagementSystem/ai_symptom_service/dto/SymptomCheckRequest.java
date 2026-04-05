package com.sliit.hospitalManagementSystem.ai_symptom_service.dto;

import lombok.Data;

@Data
public class SymptomCheckRequest {
    private String symptoms;
    private String additionalInfo; // age, gender, duration
}
