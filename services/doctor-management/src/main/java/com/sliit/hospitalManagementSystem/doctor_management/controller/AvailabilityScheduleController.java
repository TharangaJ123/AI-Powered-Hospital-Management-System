package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.dto.AvailabilityScheduleDTO;
import com.sliit.hospitalManagementSystem.doctor_management.service.AvailabilityScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/doctors/availability")
@CrossOrigin(origins = "*")
public class AvailabilityScheduleController {

    private final AvailabilityScheduleService availabilityScheduleService;

    public AvailabilityScheduleController(AvailabilityScheduleService availabilityScheduleService) {
        this.availabilityScheduleService = availabilityScheduleService;
    }

    @PostMapping
    public ResponseEntity<AvailabilityScheduleDTO> createSchedule(@Valid @RequestBody AvailabilityScheduleDTO dto) {
        AvailabilityScheduleDTO created = availabilityScheduleService.createSchedule(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AvailabilityScheduleDTO>> getSchedulesByDoctorId(@PathVariable Long doctorId) {
        List<AvailabilityScheduleDTO> schedules = availabilityScheduleService.getSchedulesByDoctorId(doctorId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}")
    public ResponseEntity<List<AvailabilityScheduleDTO>> getSchedulesByDoctorIdAndDay(
            @PathVariable Long doctorId, @PathVariable DayOfWeek dayOfWeek) {
        List<AvailabilityScheduleDTO> schedules = availabilityScheduleService.getSchedulesByDoctorIdAndDay(doctorId, dayOfWeek);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/doctor/{doctorId}/available")
    public ResponseEntity<List<AvailabilityScheduleDTO>> getAvailableSchedules(@PathVariable Long doctorId) {
        List<AvailabilityScheduleDTO> schedules = availabilityScheduleService.getAvailableSchedules(doctorId);
        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityScheduleDTO> updateSchedule(
            @PathVariable Long id, @Valid @RequestBody AvailabilityScheduleDTO dto) {
        AvailabilityScheduleDTO updated = availabilityScheduleService.updateSchedule(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        availabilityScheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/doctor/{doctorId}")
    public ResponseEntity<Void> deleteAllSchedulesByDoctorId(@PathVariable Long doctorId) {
        availabilityScheduleService.deleteAllSchedulesByDoctorId(doctorId);
        return ResponseEntity.noContent().build();
    }
}
