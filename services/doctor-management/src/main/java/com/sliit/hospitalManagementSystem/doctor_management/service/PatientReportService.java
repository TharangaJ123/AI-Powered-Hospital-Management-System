package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.PatientReportDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.PatientReport;
import com.sliit.hospitalManagementSystem.doctor_management.repository.PatientReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientReportService {

    private final PatientReportRepository patientReportRepository;

    public PatientReportService(PatientReportRepository patientReportRepository) {
        this.patientReportRepository = patientReportRepository;
    }

    public PatientReportDTO createReport(PatientReportDTO dto) {
        PatientReport report = mapToEntity(dto);
        PatientReport saved = patientReportRepository.save(report);
        return mapToDTO(saved);
    }

    public PatientReportDTO getReportById(Long id) {
        PatientReport report = patientReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient report not found with id: " + id));
        return mapToDTO(report);
    }

    public List<PatientReportDTO> getReportsByDoctorId(Long doctorId) {
        return patientReportRepository.findByDoctorIdOrderByUploadedAtDesc(doctorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PatientReportDTO> getReportsByPatientId(Long patientId) {
        return patientReportRepository.findByPatientId(patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PatientReportDTO> getReportsByDoctorAndPatient(Long doctorId, Long patientId) {
        return patientReportRepository.findByDoctorIdAndPatientId(doctorId, patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PatientReportDTO addDoctorRemarks(Long id, String remarks) {
        PatientReport report = patientReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient report not found with id: " + id));

        report.setDoctorRemarks(remarks);

        PatientReport updated = patientReportRepository.save(report);
        return mapToDTO(updated);
    }

    // --- Mapping helpers ---

    private PatientReportDTO mapToDTO(PatientReport report) {
        return PatientReportDTO.builder()
                .id(report.getId())
                .doctorId(report.getDoctorId())
                .patientId(report.getPatientId())
                .reportTitle(report.getReportTitle())
                .reportType(report.getReportType())
                .fileUrl(report.getFileUrl())
                .fileName(report.getFileName())
                .description(report.getDescription())
                .doctorRemarks(report.getDoctorRemarks())
                .uploadedAt(report.getUploadedAt())
                .build();
    }

    private PatientReport mapToEntity(PatientReportDTO dto) {
        return PatientReport.builder()
                .doctorId(dto.getDoctorId())
                .patientId(dto.getPatientId())
                .reportTitle(dto.getReportTitle())
                .reportType(dto.getReportType())
                .fileUrl(dto.getFileUrl())
                .fileName(dto.getFileName())
                .description(dto.getDescription())
                .doctorRemarks(dto.getDoctorRemarks())
                .build();
    }
}
