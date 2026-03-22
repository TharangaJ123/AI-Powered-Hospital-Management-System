package com.sliit.appointment_service.controller;

import com.sliit.appointment_service.dto.AppointmentRequestDto;
import com.sliit.appointment_service.dto.AppointmentResponseDto;
import com.sliit.appointment_service.dto.DoctorDto;
import com.sliit.appointment_service.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/doctors/search")
    public ResponseEntity<List<DoctorDto>> searchDoctorsBySpecialty(@RequestParam String specialty) {
        return ResponseEntity.ok(appointmentService.searchDoctorsBySpecialty(specialty));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDto> bookAppointment(@RequestBody AppointmentRequestDto request) {
        return new ResponseEntity<>(appointmentService.bookAppointment(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequestDto request) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointmentStatus(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentStatus(id));
    }
    
    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDto> completeAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId));
    }
}
