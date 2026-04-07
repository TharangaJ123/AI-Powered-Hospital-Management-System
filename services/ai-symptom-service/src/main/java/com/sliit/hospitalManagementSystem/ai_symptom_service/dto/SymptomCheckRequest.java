package com.sliit.hospitalManagementSystem.ai_symptom_service.dto;

import lombok.Data;

@Data
public class SymptomCheckRequest {
    private String symptoms;
    private int age;
    private String gender;
    private String medicalHistory; // any hidden or current diseases
}
