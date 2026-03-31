package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.dto.PrescriptionDTO;
import com.sliit.hospitalManagementSystem.doctor_management.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/prescriptions")
@CrossOrigin(origins = "*")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public ResponseEntity<PrescriptionDTO> createPrescription(@Valid @RequestBody PrescriptionDTO dto) {
        PrescriptionDTO created = prescriptionService.createPrescription(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> getPrescriptionById(@PathVariable Long id) {
        PrescriptionDTO prescription = prescriptionService.getPrescriptionById(id);
        return ResponseEntity.ok(prescription);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByDoctorId(@PathVariable Long doctorId) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByDoctorId(doctorId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByPatientId(@PathVariable Long patientId) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByPatientId(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/doctor/{doctorId}/patient/{patientId}")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByDoctorAndPatient(
            @PathVariable Long doctorId, @PathVariable Long patientId) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByDoctorAndPatient(doctorId, patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> updatePrescription(
            @PathVariable Long id, @Valid @RequestBody PrescriptionDTO dto) {
        PrescriptionDTO updated = prescriptionService.updatePrescription(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }
}
