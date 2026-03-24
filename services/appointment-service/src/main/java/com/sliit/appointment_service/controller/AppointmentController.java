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

    // Search for doctors by their specialty (e.g., Cardiologist)
    @GetMapping("/doctors/search")
    public ResponseEntity<List<DoctorDto>> searchDoctorsBySpecialty(@RequestParam String specialty) {
        return ResponseEntity.ok(appointmentService.searchDoctorsBySpecialty(specialty));
    }

    // Book a new appointment for a patient with a specific doctor
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> bookAppointment(@RequestBody AppointmentRequestDto request) {
        return new ResponseEntity<>(appointmentService.bookAppointment(request), HttpStatus.CREATED);
    }

    // Update the details (date, doctor, etc.) of an existing appointment
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequestDto request) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request));
    }

    // Cancel an appointment by changing its status to CANCELLED
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    // Retrieve the current details and status of a specific appointment
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointmentStatus(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentStatus(id));
    }
    
    // Mark an appointment as COMPLETED when the visit is finished
    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDto> completeAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }

    // Retrieve a complete list of all appointments in the system
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    // Permanently delete an appointment entirely from the database
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    // Retrieve all appointments scheduled for a specific patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId));
    }

    // Retrieve all appointments for a specific doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId));
    }

    // Doctor accepting an appointment request
    @PutMapping("/{id}/accept")
    public ResponseEntity<AppointmentResponseDto> acceptAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.acceptAppointment(id));
    }

    // Doctor rejecting an appointment request
    @PutMapping("/{id}/reject")
    public ResponseEntity<AppointmentResponseDto> rejectAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.rejectAppointment(id));
    }
}
