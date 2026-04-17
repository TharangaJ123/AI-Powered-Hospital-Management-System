package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.model.DoctorLeave;
import com.sliit.hospitalManagementSystem.doctor_management.model.LeaveStatus;
import com.sliit.hospitalManagementSystem.doctor_management.repository.DoctorLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/leaves")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorLeaveController {

    // Repository for persistence operations on doctor leave records
    private final DoctorLeaveRepository doctorLeaveRepository;

    // Retrieves all leave requests associated with a specific doctor ID
    @GetMapping("/doctor/{doctorId}")
    public List<DoctorLeave> getLeavesByDoctor(@PathVariable Long doctorId) {
        return doctorLeaveRepository.findByDoctorId(doctorId);
    }

    // Submits a new leave request, defaulting its status to PENDING
    @PostMapping
    public DoctorLeave requestLeave(@RequestBody DoctorLeave leave) {
        leave.setStatus(LeaveStatus.PENDING);
        return doctorLeaveRepository.save(leave);
    }

    // Updates the approval status (e.g., APPROVED, REJECTED) of a specific leave request
    @PutMapping("/{id}/status")
    public ResponseEntity<DoctorLeave> updateLeaveStatus(@PathVariable("id") @org.springframework.lang.NonNull Long id, @RequestParam LeaveStatus status) {
        return doctorLeaveRepository.findById(id)
                .map(leave -> {
                    leave.setStatus(status);
                    return ResponseEntity.ok(doctorLeaveRepository.save(leave));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Retrieves a master list of all leave requests from all doctors
    @GetMapping
    public List<DoctorLeave> getAllLeaves() {
        return doctorLeaveRepository.findAll();
    }
}
