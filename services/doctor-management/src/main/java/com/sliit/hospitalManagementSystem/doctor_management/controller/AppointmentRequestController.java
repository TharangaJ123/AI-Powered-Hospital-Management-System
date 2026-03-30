package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.dto.AppointmentRequestDTO;
import com.sliit.hospitalManagementSystem.doctor_management.service.AppointmentRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors/appointments")
@CrossOrigin(origins = "*")
public class AppointmentRequestController {

    private final AppointmentRequestService appointmentRequestService;

    public AppointmentRequestController(AppointmentRequestService appointmentRequestService) {
        this.appointmentRequestService = appointmentRequestService;
    }

    @PostMapping
    public ResponseEntity<AppointmentRequestDTO> createRequest(@Valid @RequestBody AppointmentRequestDTO dto) {
        AppointmentRequestDTO created = appointmentRequestService.createRequest(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentRequestDTO> getRequestById(@PathVariable Long id) {
        AppointmentRequestDTO request = appointmentRequestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentRequestDTO>> getRequestsByDoctorId(@PathVariable Long doctorId) {
        List<AppointmentRequestDTO> requests = appointmentRequestService.getRequestsByDoctorId(doctorId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/doctor/{doctorId}/pending")
    public ResponseEntity<List<AppointmentRequestDTO>> getPendingRequestsByDoctorId(@PathVariable Long doctorId) {
        List<AppointmentRequestDTO> requests = appointmentRequestService.getPendingRequestsByDoctorId(doctorId);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<AppointmentRequestDTO> acceptRequest(
            @PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("doctorNotes") : null;
        AppointmentRequestDTO accepted = appointmentRequestService.acceptRequest(id, notes);
        return ResponseEntity.ok(accepted);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<AppointmentRequestDTO> rejectRequest(
            @PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("doctorNotes") : null;
        AppointmentRequestDTO rejected = appointmentRequestService.rejectRequest(id, notes);
        return ResponseEntity.ok(rejected);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentRequestDTO> completeRequest(@PathVariable Long id) {
        AppointmentRequestDTO completed = appointmentRequestService.completeRequest(id);
        return ResponseEntity.ok(completed);
    }
}
