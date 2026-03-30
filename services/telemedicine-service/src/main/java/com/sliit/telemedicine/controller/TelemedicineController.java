package com.sliit.telemedicine.controller;

import com.sliit.telemedicine.dto.TelemedicineSessionResponse;
import com.sliit.telemedicine.model.TelemedicineSession;
import com.sliit.telemedicine.service.TelemedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/telemedicine/sessions")
@RequiredArgsConstructor
public class TelemedicineController {

    private final TelemedicineService telemedicineService;

    @PostMapping("/{appointmentId}")
    public ResponseEntity<TelemedicineSession> createSession(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(telemedicineService.createOrGetSession(appointmentId));
    }

    @GetMapping("/{appointmentId}/join")
    public ResponseEntity<TelemedicineSessionResponse> joinSession(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(telemedicineService.getJoinSessionDetails(appointmentId));
    }

    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<TelemedicineSession> completeSession(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(telemedicineService.completeSession(appointmentId));
    }
}
