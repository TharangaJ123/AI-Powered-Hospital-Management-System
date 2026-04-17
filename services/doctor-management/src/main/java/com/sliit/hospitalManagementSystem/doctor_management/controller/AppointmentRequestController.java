package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.dto.AppointmentRequestDTO;
import com.sliit.hospitalManagementSystem.doctor_management.service.AppointmentRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor/appointments")
@CrossOrigin(origins = "*")
public class AppointmentRequestController {

    // Service for managing appointment requests from the doctor's perspective
    private final AppointmentRequestService appointmentRequestService;

    // Constructor based dependency injection
    public AppointmentRequestController(AppointmentRequestService appointmentRequestService) {
        this.appointmentRequestService = appointmentRequestService;
    }

    // Endpoint to create a new appointment request
    @PostMapping
    public ResponseEntity<AppointmentRequestDTO> createRequest(@Valid @RequestBody AppointmentRequestDTO dto) {
        AppointmentRequestDTO created = appointmentRequestService.createRequest(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Retrieves a specific appointment request by its unique ID
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentRequestDTO> getRequestById(@PathVariable @NonNull Long id) {
        AppointmentRequestDTO request = appointmentRequestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    // Retrieves all appointment requests associated with a specific doctor ID
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentRequestDTO>> getRequestsByDoctorId(@PathVariable @NonNull Long doctorId) {
        List<AppointmentRequestDTO> requests = appointmentRequestService.getRequestsByDoctorId(doctorId);
        return ResponseEntity.ok(requests);
    }

    // Retrieves only the pending appointment requests for a specific doctor
    @GetMapping("/doctor/{doctorId}/pending")
    public ResponseEntity<List<AppointmentRequestDTO>> getPendingRequestsByDoctorId(@PathVariable @NonNull Long doctorId) {
        List<AppointmentRequestDTO> requests = appointmentRequestService.getPendingRequestsByDoctorId(doctorId);
        return ResponseEntity.ok(requests);
    }

    // Transitions an appointment request to the ACCEPTED status and optionally adds notes
    @PutMapping("/{id}/accept")
    public ResponseEntity<AppointmentRequestDTO> acceptRequest(
            @PathVariable @NonNull Long id, @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("doctorNotes") : null;
        AppointmentRequestDTO accepted = appointmentRequestService.acceptRequest(id, notes);
        return ResponseEntity.ok(accepted);
    }

    // Transitions an appointment request to the REJECTED status and optionally adds notes
    @PutMapping("/{id}/reject")
    public ResponseEntity<AppointmentRequestDTO> rejectRequest(
            @PathVariable @NonNull Long id, @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("doctorNotes") : null;
        AppointmentRequestDTO rejected = appointmentRequestService.rejectRequest(id, notes);
        return ResponseEntity.ok(rejected);
    }

    // Marks an appointment as COMPLETED after the medical consultation is finished
    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentRequestDTO> completeRequest(@PathVariable @NonNull Long id) {
        AppointmentRequestDTO completed = appointmentRequestService.completeRequest(id);
        return ResponseEntity.ok(completed);
    }
}
