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

    private final AiSymptomService aiSymptomService;

    @PostMapping("/check-symptoms")
    public ResponseEntity<SymptomCheckResponse> checkSymptoms(@RequestBody SymptomCheckRequest request) {
        return ResponseEntity.ok(aiSymptomService.checkSymptoms(request));
    }
}
