package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.dto.TelemedicineSessionDTO;
import com.sliit.hospitalManagementSystem.doctor_management.service.TelemedicineSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors/telemedicine")
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class TelemedicineSessionController {

    // Service for managing virtual consultation sessions between doctors and patients
    private final TelemedicineSessionService telemedicineSessionService;

    // Constructor based dependency injection
    public TelemedicineSessionController(TelemedicineSessionService telemedicineSessionService) {
        this.telemedicineSessionService = telemedicineSessionService;
    }

    // Schedules a new telemedicine session
    @PostMapping
    public ResponseEntity<TelemedicineSessionDTO> createSession(@Valid @RequestBody TelemedicineSessionDTO dto) {
        TelemedicineSessionDTO created = telemedicineSessionService.createSession(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Retrieves specific telemedicine session details by its ID
    @GetMapping("/{id}")
    public ResponseEntity<TelemedicineSessionDTO> getSessionById(@PathVariable Long id) {
        TelemedicineSessionDTO session = telemedicineSessionService.getSessionById(id);
        return ResponseEntity.ok(session);
    }

    // Fetches all telemedicine sessions associated with a specific doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<TelemedicineSessionDTO>> getSessionsByDoctorId(@PathVariable Long doctorId) {
        List<TelemedicineSessionDTO> sessions = telemedicineSessionService.getSessionsByDoctorId(doctorId);
        return ResponseEntity.ok(sessions);
    }

    // Retrieves only the sessions that are scheduled for a future date/time for a doctor
    @GetMapping("/doctor/{doctorId}/upcoming")
    public ResponseEntity<List<TelemedicineSessionDTO>> getUpcomingSessionsByDoctorId(@PathVariable Long doctorId) {
        List<TelemedicineSessionDTO> sessions = telemedicineSessionService.getUpcomingSessionsByDoctorId(doctorId);
        return ResponseEntity.ok(sessions);
    }

    // Marks a telemedicine session as started/active
    @PutMapping("/{id}/start")
    public ResponseEntity<TelemedicineSessionDTO> startSession(@PathVariable Long id) {
        TelemedicineSessionDTO started = telemedicineSessionService.startSession(id);
        return ResponseEntity.ok(started);
    }

    // Concludes an active telemedicine session and optionally adds session notes
    @PutMapping("/{id}/end")
    public ResponseEntity<TelemedicineSessionDTO> endSession(
            @PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        TelemedicineSessionDTO ended = telemedicineSessionService.endSession(id, notes);
        return ResponseEntity.ok(ended);
    }

    // Cancels a scheduled telemedicine session
    @PutMapping("/{id}/cancel")
    public ResponseEntity<TelemedicineSessionDTO> cancelSession(@PathVariable Long id) {
        TelemedicineSessionDTO cancelled = telemedicineSessionService.cancelSession(id);
        return ResponseEntity.ok(cancelled);
    }
}
