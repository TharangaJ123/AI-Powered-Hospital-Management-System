package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.dto.PrescriptionDTO;
import com.sliit.hospitalManagementSystem.doctor_management.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/prescriptions")
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class PrescriptionController {

    // Service for managing digital prescriptions issued by doctors
    private final PrescriptionService prescriptionService;

    // Constructor based dependency injection
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    // Issues a new prescription for a patient
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionDTO> createPrescription(@Valid @RequestBody PrescriptionDTO dto) {
        PrescriptionDTO created = prescriptionService.createPrescription(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Retrieves a specific prescription by its unique ID
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> getPrescriptionById(@PathVariable Long id) {
        PrescriptionDTO prescription = prescriptionService.getPrescriptionById(id);
        return ResponseEntity.ok(prescription);
    }

    // Retrieves all prescriptions issued by a specific doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByDoctorId(@PathVariable Long doctorId) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByDoctorId(doctorId);
        return ResponseEntity.ok(prescriptions);
    }

    // Retrieves all prescriptions assigned to a specific patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByPatientId(@PathVariable Long patientId) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByPatientId(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    // Retrieves prescriptions issued by a specific doctor to a specific patient
    @GetMapping("/doctor/{doctorId}/patient/{patientId}")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByDoctorAndPatient(
            @PathVariable Long doctorId, @PathVariable Long patientId) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByDoctorAndPatient(doctorId, patientId);
        return ResponseEntity.ok(prescriptions);
    }

    // Updates an existing prescription's details
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionDTO> updatePrescription(
            @PathVariable Long id, @Valid @RequestBody PrescriptionDTO dto) {
        PrescriptionDTO updated = prescriptionService.updatePrescription(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Permanently removes a prescription record
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }
}
