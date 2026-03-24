package com.sliit.user_management.controller;

import com.sliit.user_management.dto.*;
import com.sliit.user_management.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<DoctorProfileDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorProfileDto>> findBySpecialty(@RequestParam String specialty) {
        return ResponseEntity.ok(doctorService.findBySpecialty(specialty));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<DoctorProfileDto> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(doctorService.getProfile(userId));
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<DoctorProfileDto> updateProfile(@PathVariable Long userId, @RequestBody DoctorProfileDto req) {
        return ResponseEntity.ok(doctorService.updateProfile(userId, req));
    }

    @PostMapping("/{doctorId}/availability")
    public ResponseEntity<DoctorAvailabilityDto> addAvailability(@PathVariable Long doctorId, @RequestBody DoctorAvailabilityDto req) {
        return ResponseEntity.ok(doctorService.addAvailability(doctorId, req));
    }

    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<List<DoctorAvailabilityDto>> getAvailability(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorService.getAvailability(doctorId));
    }

    @PostMapping("/{doctorId}/prescriptions")
    public ResponseEntity<PrescriptionDto> issuePrescription(@PathVariable Long doctorId, @RequestBody PrescriptionDto req) {
        return ResponseEntity.ok(doctorService.issuePrescription(doctorId, req));
    }

    @GetMapping("/{doctorId}/patients/{patientId}/reports")
    public ResponseEntity<List<MedicalDocumentDto>> viewPatientReports(@PathVariable Long doctorId, @PathVariable Long patientId) {
        // Here, doctorId could be used for authorization to ensure they can see this patient's records
        return ResponseEntity.ok(doctorService.viewPatientReports(patientId));
    }
}
