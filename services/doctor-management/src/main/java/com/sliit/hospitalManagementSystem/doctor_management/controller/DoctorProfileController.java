package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.dto.DoctorProfileDTO;
import com.sliit.hospitalManagementSystem.doctor_management.service.DoctorProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/profiles")
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class DoctorProfileController {

    // Service for handling doctor profile creation, updates, and lookups
    private final DoctorProfileService doctorProfileService;

    // Constructor based dependency injection
    public DoctorProfileController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    // Creates a new doctor profile record
    @PostMapping
    public ResponseEntity<DoctorProfileDTO> createProfile(@Valid @RequestBody DoctorProfileDTO dto) {
        DoctorProfileDTO created = doctorProfileService.createProfile(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Fetches a specific doctor profile using its unique ID
    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfileDTO> getProfileById(@PathVariable Long id) {
        DoctorProfileDTO profile = doctorProfileService.getProfileById(id);
        return ResponseEntity.ok(profile);
    }

    // Fetches a doctor profile associated with a specific system user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<DoctorProfileDTO> getProfileByUserId(@PathVariable Long userId) {
        DoctorProfileDTO profile = doctorProfileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    // Retrieves a master list of all doctor profiles in the system
    @GetMapping
    public ResponseEntity<List<DoctorProfileDTO>> getAllProfiles() {
        List<DoctorProfileDTO> profiles = doctorProfileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    // Searches for doctors by their medical specialization
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<DoctorProfileDTO>> getProfilesBySpecialization(@PathVariable String specialization) {
        List<DoctorProfileDTO> profiles = doctorProfileService.getProfilesBySpecialization(specialization);
        return ResponseEntity.ok(profiles);
    }

    // Filters doctor profiles by their current registration status (e.g., PENDING, APPROVED)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DoctorProfileDTO>> getProfilesByStatus(@PathVariable String status) {
        List<DoctorProfileDTO> profiles = doctorProfileService.getProfilesByStatus(status);
        return ResponseEntity.ok(profiles);
    }

    // Endpoint for administrators to approve a pending doctor registration
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorProfileDTO> approveDoctor(@PathVariable Long id) {
        DoctorProfileDTO updated = doctorProfileService.approveDoctor(id);
        return ResponseEntity.ok(updated);
    }

    // Retrieves a list of doctors who offer telemedicine consultations
    @GetMapping("/telemedicine")
    public ResponseEntity<List<DoctorProfileDTO>> getTelemedicineDoctors() {
        List<DoctorProfileDTO> profiles = doctorProfileService.getTelemedicineDoctors();
        return ResponseEntity.ok(profiles);
    }

    // Updates an existing doctor profile's information
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<DoctorProfileDTO> updateProfile(@PathVariable Long id, @Valid @RequestBody DoctorProfileDTO dto) {
        DoctorProfileDTO updated = doctorProfileService.updateProfile(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Permanently deletes a doctor profile from the system
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        doctorProfileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
}
