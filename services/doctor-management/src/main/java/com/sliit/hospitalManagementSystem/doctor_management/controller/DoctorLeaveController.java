package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.model.DoctorLeave;
import com.sliit.hospitalManagementSystem.doctor_management.model.LeaveStatus;
import com.sliit.hospitalManagementSystem.doctor_management.repository.DoctorLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-leaves")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorLeaveController {

    private final DoctorLeaveRepository doctorLeaveRepository;

    @GetMapping("/doctor/{doctorId}")
    public List<DoctorLeave> getLeavesByDoctor(@PathVariable Long doctorId) {
        return doctorLeaveRepository.findByDoctorId(doctorId);
    }

    @PostMapping
    public DoctorLeave requestLeave(@RequestBody DoctorLeave leave) {
        leave.setStatus(LeaveStatus.PENDING);
        return doctorLeaveRepository.save(leave);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DoctorLeave> updateLeaveStatus(@PathVariable("id") Long id, @RequestParam LeaveStatus status) {
        return doctorLeaveRepository.findById(id)
                .map(leave -> {
                    leave.setStatus(status);
                    return ResponseEntity.ok(doctorLeaveRepository.save(leave));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<DoctorLeave> getAllLeaves() {
        return doctorLeaveRepository.findAll();
    }
}
