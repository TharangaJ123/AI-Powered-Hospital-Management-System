package com.sliit.telemedicine.controller;

import com.sliit.telemedicine.model.TelemedicineSession;
import com.sliit.telemedicine.repository.TelemedicineSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/telemedicine/sessions")
@RequiredArgsConstructor
public class TelemedicineController {

    private final TelemedicineSessionRepository sessionRepository;

    @PostMapping("/{appointmentId}")
    public ResponseEntity<TelemedicineSession> createSession(@PathVariable Long appointmentId) {
        TelemedicineSession session = sessionRepository.findByAppointmentId(appointmentId)
                .orElseGet(() -> {
                    String roomName = "hospital-mgmt-" + appointmentId + "-" + UUID.randomUUID().toString().substring(0, 8);
                    return TelemedicineSession.builder()
                            .appointmentId(appointmentId)
                            .roomName(roomName)
                            .status(TelemedicineSession.SessionStatus.CREATED)
                            .build();
                });
        
        return ResponseEntity.ok(sessionRepository.save(session));
    }

    @GetMapping("/{appointmentId}/join")
    public ResponseEntity<String> joinSession(@PathVariable Long appointmentId) {
        TelemedicineSession session = sessionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Session not found for appointment " + appointmentId));
        
        String jitsiUrl = "https://meet.jit.si/" + session.getRoomName();
        return ResponseEntity.ok(jitsiUrl);
    }
    
    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<TelemedicineSession> completeSession(@PathVariable Long appointmentId) {
        TelemedicineSession session = sessionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        session.setStatus(TelemedicineSession.SessionStatus.COMPLETED);
        return ResponseEntity.ok(sessionRepository.save(session));
    }
}
