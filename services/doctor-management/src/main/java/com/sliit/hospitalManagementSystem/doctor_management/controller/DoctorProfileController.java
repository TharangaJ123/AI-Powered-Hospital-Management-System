package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.dto.DoctorProfileDTO;
import com.sliit.hospitalManagementSystem.doctor_management.service.DoctorProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/profiles")
@CrossOrigin(origins = "*")
public class DoctorProfileController {

    private final DoctorProfileService doctorProfileService;

    public DoctorProfileController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    @PostMapping
    public ResponseEntity<DoctorProfileDTO> createProfile(@Valid @RequestBody DoctorProfileDTO dto) {
        DoctorProfileDTO created = doctorProfileService.createProfile(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfileDTO> getProfileById(@PathVariable Long id) {
        DoctorProfileDTO profile = doctorProfileService.getProfileById(id);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<DoctorProfileDTO> getProfileByUserId(@PathVariable Long userId) {
        DoctorProfileDTO profile = doctorProfileService.getProfileByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<List<DoctorProfileDTO>> getAllProfiles() {
        List<DoctorProfileDTO> profiles = doctorProfileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<DoctorProfileDTO>> getProfilesBySpecialization(@PathVariable String specialization) {
        List<DoctorProfileDTO> profiles = doctorProfileService.getProfilesBySpecialization(specialization);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DoctorProfileDTO>> getProfilesByStatus(@PathVariable String status) {
        List<DoctorProfileDTO> profiles = doctorProfileService.getProfilesByStatus(status);
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<DoctorProfileDTO> approveDoctor(@PathVariable Long id) {
        DoctorProfileDTO updated = doctorProfileService.approveDoctor(id);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/telemedicine")
    public ResponseEntity<List<DoctorProfileDTO>> getTelemedicineDoctors() {
        List<DoctorProfileDTO> profiles = doctorProfileService.getTelemedicineDoctors();
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorProfileDTO> updateProfile(@PathVariable Long id, @Valid @RequestBody DoctorProfileDTO dto) {
        DoctorProfileDTO updated = doctorProfileService.updateProfile(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        doctorProfileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
}
