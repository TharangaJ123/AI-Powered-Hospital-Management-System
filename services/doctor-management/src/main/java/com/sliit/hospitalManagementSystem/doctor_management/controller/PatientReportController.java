package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.dto.PatientReportDTO;
import com.sliit.hospitalManagementSystem.doctor_management.service.PatientReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors/reports")
@CrossOrigin(origins = "*")
public class PatientReportController {

    private final PatientReportService patientReportService;

    public PatientReportController(PatientReportService patientReportService) {
        this.patientReportService = patientReportService;
    }

    @PostMapping
    public ResponseEntity<PatientReportDTO> createReport(@RequestBody PatientReportDTO dto) {
        PatientReportDTO created = patientReportService.createReport(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientReportDTO> getReportById(@PathVariable Long id) {
        PatientReportDTO report = patientReportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PatientReportDTO>> getReportsByDoctorId(@PathVariable Long doctorId) {
        List<PatientReportDTO> reports = patientReportService.getReportsByDoctorId(doctorId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientReportDTO>> getReportsByPatientId(@PathVariable Long patientId) {
        List<PatientReportDTO> reports = patientReportService.getReportsByPatientId(patientId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/doctor/{doctorId}/patient/{patientId}")
    public ResponseEntity<List<PatientReportDTO>> getReportsByDoctorAndPatient(
            @PathVariable Long doctorId, @PathVariable Long patientId) {
        List<PatientReportDTO> reports = patientReportService.getReportsByDoctorAndPatient(doctorId, patientId);
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/{id}/remarks")
    public ResponseEntity<PatientReportDTO> addDoctorRemarks(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        String remarks = body.get("doctorRemarks");
        PatientReportDTO updated = patientReportService.addDoctorRemarks(id, remarks);
        return ResponseEntity.ok(updated);
    }
}
