package com.sliit.user_management.controller;

import com.sliit.user_management.dto.*;
import com.sliit.user_management.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerPatient(@RequestBody UserRegistrationDto req) {
        return ResponseEntity.ok(patientService.registerPatient(req));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<PatientProfileDto> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(patientService.getProfile(userId));
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<PatientProfileDto> updateProfile(@PathVariable Long userId, @RequestBody PatientProfileDto req) {
        return ResponseEntity.ok(patientService.updateProfile(userId, req));
    }

    @PostMapping("/{patientId}/documents")
    public ResponseEntity<MedicalDocumentDto> uploadDocument(@PathVariable Long patientId, @RequestBody MedicalDocumentDto req) {
        return ResponseEntity.ok(patientService.uploadDocument(patientId, req));
    }

    @GetMapping("/{patientId}/documents")
    public ResponseEntity<List<MedicalDocumentDto>> getDocuments(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatientDocuments(patientId));
    }

    @GetMapping("/{patientId}/history")
    public ResponseEntity<List<String>> getMedicalHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getMedicalHistory(patientId));
    }
}
