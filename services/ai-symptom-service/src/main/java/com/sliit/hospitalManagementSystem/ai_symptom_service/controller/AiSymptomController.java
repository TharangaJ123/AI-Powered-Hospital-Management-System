package com.sliit.hospitalManagementSystem.ai_symptom_service.controller;

import com.sliit.hospitalManagementSystem.ai_symptom_service.dto.SymptomCheckRequest;
import com.sliit.hospitalManagementSystem.ai_symptom_service.dto.SymptomCheckResponse;
import com.sliit.hospitalManagementSystem.ai_symptom_service.service.AiSymptomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiSymptomController {

    // Injecting AiSymptomService to handle the logic for symptom analysis
    private final AiSymptomService aiSymptomService;

    // Endpoint to receive symptoms and patient data, returning an AI-generated diagnosis
    @PostMapping("/check-symptoms")
    public ResponseEntity<SymptomCheckResponse> checkSymptoms(@RequestBody SymptomCheckRequest request) {
        return ResponseEntity.ok(aiSymptomService.checkSymptoms(request));
    }
}