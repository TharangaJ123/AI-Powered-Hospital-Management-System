package com.sliit.telemedicine.controller;

import com.sliit.telemedicine.dto.TelemedicineSessionResponse;
import com.sliit.telemedicine.model.TelemedicineSession;
import com.sliit.telemedicine.service.TelemedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/telemedicine/sessions")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class TelemedicineController {

    // Injecting TelemedicineService to handle session-related logic
    private final TelemedicineService telemedicineService;

    // Endpoint for doctors to create or retrieve a telemedicine session for an appointment
    @PostMapping("/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<TelemedicineSession> createSession(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(telemedicineService.createOrGetSession(appointmentId));
    }

    // Endpoint for both patients and doctors to get details needed to join a session
    @GetMapping("/{appointmentId}/join")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ResponseEntity<TelemedicineSessionResponse> joinSession(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(telemedicineService.getJoinSessionDetails(appointmentId));
    }

    // Endpoint for doctors to mark a telemedicine session as completed
    @PutMapping("/{appointmentId}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<TelemedicineSession> completeSession(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(telemedicineService.completeSession(appointmentId));
    }
}
