package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.dto.AvailabilityScheduleDTO;
import com.sliit.hospitalManagementSystem.doctor_management.service.AvailabilityScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/doctors/availability")
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class AvailabilityScheduleController {

    // Service for managing the weekly recurring availability schedules of doctors
    private final AvailabilityScheduleService availabilityScheduleService;

    // Constructor based dependency injection
    public AvailabilityScheduleController(AvailabilityScheduleService availabilityScheduleService) {
        this.availabilityScheduleService = availabilityScheduleService;
    }

    // Creates a new recurring availability slot for a doctor (e.g., Mondays 9am-12pm)
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AvailabilityScheduleDTO> createSchedule(@Valid @RequestBody AvailabilityScheduleDTO dto) {
        AvailabilityScheduleDTO created = availabilityScheduleService.createSchedule(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Fetches all availability schedules defined for a specific doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AvailabilityScheduleDTO>> getSchedulesByDoctorId(@PathVariable Long doctorId) {
        List<AvailabilityScheduleDTO> schedules = availabilityScheduleService.getSchedulesByDoctorId(doctorId);
        return ResponseEntity.ok(schedules);
    }

    // Fetches availability schedules for a specific doctor filtered by the day of the week
    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}")
    public ResponseEntity<List<AvailabilityScheduleDTO>> getSchedulesByDoctorIdAndDay(
            @PathVariable Long doctorId, @PathVariable DayOfWeek dayOfWeek) {
        List<AvailabilityScheduleDTO> schedules = availabilityScheduleService.getSchedulesByDoctorIdAndDay(doctorId, dayOfWeek);
        return ResponseEntity.ok(schedules);
    }

    // Retrieves only the currently active and available time slots for a doctor
    @GetMapping("/doctor/{doctorId}/available")
    public ResponseEntity<List<AvailabilityScheduleDTO>> getAvailableSchedules(@PathVariable Long doctorId) {
        List<AvailabilityScheduleDTO> schedules = availabilityScheduleService.getAvailableSchedules(doctorId);
        return ResponseEntity.ok(schedules);
    }

    // Updates an existing availability schedule slot by its ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AvailabilityScheduleDTO> updateSchedule(
            @PathVariable Long id, @Valid @RequestBody AvailabilityScheduleDTO dto) {
        AvailabilityScheduleDTO updated = availabilityScheduleService.updateSchedule(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Permanently removes a specific availability schedule slot
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        availabilityScheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    // Removes all availability schedules for a specific doctor
    @DeleteMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteAllSchedulesByDoctorId(@PathVariable Long doctorId) {
        availabilityScheduleService.deleteAllSchedulesByDoctorId(doctorId);
        return ResponseEntity.noContent().build();
    }
}
