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

    private final TelemedicineSessionService telemedicineSessionService;

    public TelemedicineSessionController(TelemedicineSessionService telemedicineSessionService) {
        this.telemedicineSessionService = telemedicineSessionService;
    }

    @PostMapping
    public ResponseEntity<TelemedicineSessionDTO> createSession(@Valid @RequestBody TelemedicineSessionDTO dto) {
        TelemedicineSessionDTO created = telemedicineSessionService.createSession(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TelemedicineSessionDTO> getSessionById(@PathVariable Long id) {
        TelemedicineSessionDTO session = telemedicineSessionService.getSessionById(id);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<TelemedicineSessionDTO>> getSessionsByDoctorId(@PathVariable Long doctorId) {
        List<TelemedicineSessionDTO> sessions = telemedicineSessionService.getSessionsByDoctorId(doctorId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/doctor/{doctorId}/upcoming")
    public ResponseEntity<List<TelemedicineSessionDTO>> getUpcomingSessionsByDoctorId(@PathVariable Long doctorId) {
        List<TelemedicineSessionDTO> sessions = telemedicineSessionService.getUpcomingSessionsByDoctorId(doctorId);
        return ResponseEntity.ok(sessions);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TelemedicineSessionDTO> startSession(@PathVariable Long id) {
        TelemedicineSessionDTO started = telemedicineSessionService.startSession(id);
        return ResponseEntity.ok(started);
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<TelemedicineSessionDTO> endSession(
            @PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        TelemedicineSessionDTO ended = telemedicineSessionService.endSession(id, notes);
        return ResponseEntity.ok(ended);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<TelemedicineSessionDTO> cancelSession(@PathVariable Long id) {
        TelemedicineSessionDTO cancelled = telemedicineSessionService.cancelSession(id);
        return ResponseEntity.ok(cancelled);
    }
}
