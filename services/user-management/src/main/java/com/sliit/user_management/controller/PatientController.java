package com.sliit.user_management.controller;

import com.sliit.user_management.dto.PatientProfileDto;
import com.sliit.user_management.dto.UserRegistrationDto;
import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.dto.MedicalDocumentDto;
import com.sliit.user_management.dto.MedicalHistoryDto;
import com.sliit.user_management.dto.PrescriptionDto;
import com.sliit.user_management.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing patient-related operations.
 * Provides endpoints for patient registration, profile management, and document handling.
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * Registers a new patient in the system.
     * 
     * @param req the user registration details
     * @return a ResponseEntity containing the created UserResponseDto
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerPatient(@RequestBody UserRegistrationDto req) {
        return ResponseEntity.ok(patientService.registerPatient(req));
    }

    /**
     * Retrieves the profile details of a specific patient.
     * 
     * @param userId the user ID of the patient
     * @return a ResponseEntity containing the PatientProfileDto
     */
    @GetMapping("/{userId}/profile")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<PatientProfileDto> getProfile(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(patientService.getProfile(userId));
    }

    /**
     * Updates the profile details for a specific patient.
     * 
     * @param userId the user ID of the patient to update
     * @param req the updated profile details
     * @return a ResponseEntity containing the updated PatientProfileDto
     */
    @PutMapping("/{userId}/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientProfileDto> updateProfile(@PathVariable Long userId, @RequestBody PatientProfileDto req) {
        return ResponseEntity.ok(patientService.updateProfile(userId, req));
    }

    /**
     * Uploads a new medical document for a patient.
     * 
     * @param patientId the profile ID of the patient
     * @param req the medical document details to upload
     * @return a ResponseEntity containing the saved MedicalDocumentDto
     */
    @PostMapping("/{patientId}/documents")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<MedicalDocumentDto> uploadDocument(@PathVariable Long patientId, @RequestBody MedicalDocumentDto req) {
        return ResponseEntity.ok(patientService.uploadDocument(patientId, req));
    }

    /**
     * Retrieves all uploaded medical documents for a specific patient.
     * 
     * @param patientId the profile ID of the patient
     * @return a ResponseEntity containing a list of MedicalDocumentDto objects
     */
    @GetMapping("/{patientId}/documents")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<MedicalDocumentDto>> getDocuments(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatientDocuments(patientId));
    }

    /**
     * Retrieves the medical history for a specific patient.
     * 
     * @param patientId the profile ID of the patient
     * @return a ResponseEntity containing a list of MedicalHistoryDto objects
     */
    @GetMapping("/{patientId}/history")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<MedicalHistoryDto>> getMedicalHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatientMedicalHistory(patientId));
    }

    /**
     * Retrieves all prescriptions for a specific patient.
     * 
     * @param patientId the profile ID of the patient
     * @return a ResponseEntity containing a list of PrescriptionDto objects
     */
    @GetMapping("/{patientId}/prescriptions")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<PrescriptionDto>> getPrescriptions(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getPatientPrescriptions(patientId));
    }


}
