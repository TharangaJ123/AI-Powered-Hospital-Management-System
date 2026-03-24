package com.sliit.hospitalManagementSystem.telemedicine.controller;

import com.sliit.hospitalManagementSystem.telemedicine.dto.SessionRequestDTO;
import com.sliit.hospitalManagementSystem.telemedicine.dto.SessionResponseDTO;
import com.sliit.hospitalManagementSystem.telemedicine.service.TelemedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemedicine/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TelemedicineController {

    private final TelemedicineService telemedicineService;

    // POST /api/telemedicine/sessions
    // Create a new telemedicine session and get a Jitsi Meet URL
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<SessionResponseDTO> createSession(@RequestBody SessionRequestDTO request) {
        SessionResponseDTO response = telemedicineService.createSession(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /api/telemedicine/sessions/all
    // List all sessions
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SessionResponseDTO>> getAllSessions() {
        List<SessionResponseDTO> sessions = telemedicineService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    // GET /api/telemedicine/sessions/{appointmentId}
    // Get a session by appointment ID
    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<SessionResponseDTO> getSessionByAppointmentId(@PathVariable String appointmentId) {
        SessionResponseDTO session = telemedicineService.getSessionByAppointmentId(appointmentId);
        return ResponseEntity.ok(session);
    }

    // GET /api/telemedicine/sessions/doctor/{doctorId}
    // Get all sessions for a specific doctor
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<SessionResponseDTO>> getSessionsByDoctor(@PathVariable String doctorId) {
        List<SessionResponseDTO> sessions = telemedicineService.getSessionsByDoctor(doctorId);
        return ResponseEntity.ok(sessions);
    }

    // GET /api/telemedicine/sessions/patient/{patientId}
    // Get all sessions for a specific patient
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<SessionResponseDTO>> getSessionsByPatient(@PathVariable String patientId) {
        List<SessionResponseDTO> sessions = telemedicineService.getSessionsByPatient(patientId);
        return ResponseEntity.ok(sessions);
    }

    // PUT /api/telemedicine/sessions/{id}/end
    // End a session (set status = ENDED)
    @PutMapping("/{id}/end")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<SessionResponseDTO> endSession(@PathVariable Long id) {
        SessionResponseDTO response = telemedicineService.endSession(id);
        return ResponseEntity.ok(response);
    }

    // DELETE /api/telemedicine/sessions/{id}
    // Delete a session record
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSession(@PathVariable Long id) {
        telemedicineService.deleteSession(id);
        return ResponseEntity.ok("Session with id " + id + " deleted successfully.");
    }
}
