package com.sliit.appointment_service.controller;

import com.sliit.appointment_service.dto.AppointmentRequestDto;
import com.sliit.appointment_service.dto.AppointmentResponseDto;
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



    // Book a new appointment for a patient with a specific doctor
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> bookAppointment(@RequestBody AppointmentRequestDto request) {
        return new ResponseEntity<>(appointmentService.bookAppointment(request), HttpStatus.CREATED);
    }

    // Update the details (date, patient, etc.) of an existing appointment
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

    // Verify if a completed appointment exists between a patient and a doctor
    @GetMapping("/verify-completed")
    public ResponseEntity<Boolean> verifyCompletedAppointment(@RequestParam Long patientId, @RequestParam Long doctorId) {
        return ResponseEntity.ok(appointmentService.verifyCompletedAppointment(patientId, doctorId));
    }


}
