package com.sliit.appointment_service.controller;

import com.sliit.appointment_service.dto.AppointmentRequestDto;
import com.sliit.appointment_service.dto.AppointmentResponseDto;
import com.sliit.appointment_service.dto.AvailabilityCheckResponseDto;
import com.sliit.appointment_service.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    // Dependency for appointment-related business logic
    private final AppointmentService appointmentService;

    // Creates a new appointment booking in the system
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> bookAppointment(@RequestBody AppointmentRequestDto request) {
        return new ResponseEntity<>(appointmentService.bookAppointment(request), HttpStatus.CREATED);
    }

    // Modifies an existing appointment's details (date, time, etc.)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(@PathVariable Long id,
            @RequestBody AppointmentRequestDto request) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request));
    }

    // Changes the status of a specific appointment to CANCELLED
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    // Fetches the current details and status of a single appointment by ID
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointmentStatus(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentStatus(id));
    }

    // Checks if a doctor is free at a specific date and time for a new booking
    @GetMapping("/availability")
    public ResponseEntity<AvailabilityCheckResponseDto> checkDoctorAvailability(
            @RequestParam Long doctorId,
            @RequestParam LocalDate date,
            @RequestParam(required = false) String time) {
        return ResponseEntity.ok(appointmentService.checkDoctorAvailability(doctorId, date, time));
    }

    // Marks an appointment as COMPLETED after the medical visit
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<AppointmentResponseDto> completeAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }

    // Retrieves a master list of all appointments for administrative use
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    // Removes an appointment record permanently from the database
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    // Retrieves all historical and upcoming appointments for a specific patient
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId));
    }

    // Retrieves all appointments assigned to a specific doctor
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId));
    }
}
