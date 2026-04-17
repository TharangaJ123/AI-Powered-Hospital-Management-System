package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.PatientReportDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.PatientReport;
import com.sliit.hospitalManagementSystem.doctor_management.repository.PatientReportRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientReportService {

    private final PatientReportRepository patientReportRepository;

    public PatientReportService(PatientReportRepository patientReportRepository) {
        this.patientReportRepository = patientReportRepository;
    }

    // Handles the storage and indexing of a new patient medical report
    @SuppressWarnings("null")
    public PatientReportDTO createReport(PatientReportDTO dto) {
        PatientReport report = mapToEntity(dto);
        return mapToDTO(patientReportRepository.save(report));
    }

    // Retrieves a specific medical report using its unique database ID
    public PatientReportDTO getReportById(@NonNull Long id) {
        return patientReportRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Patient report not found with id: " + id));
    }

    // Fetches all medical reports authorized or managed by a particular doctor
    public List<PatientReportDTO> getReportsByDoctorId(Long doctorId) {
        return patientReportRepository.findByDoctorIdOrderByUploadedAtDesc(doctorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Retrieves the complete history of medical reports for a specific patient
    public List<PatientReportDTO> getReportsByPatientId(Long patientId) {
        return patientReportRepository.findByPatientId(patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Filters reports to find those shared between a specific doctor and patient
    public List<PatientReportDTO> getReportsByDoctorAndPatient(Long doctorId, Long patientId) {
        return patientReportRepository.findByDoctorIdAndPatientId(doctorId, patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Allows a doctor to append or update clinical remarks on an existing report
    public PatientReportDTO addDoctorRemarks(@NonNull Long id, String remarks) {
        return patientReportRepository.findById(id)
                .map(report -> {
                    report.setDoctorRemarks(remarks);
                    return mapToDTO(patientReportRepository.save(report));
                })
                .orElseThrow(() -> new RuntimeException("Patient report not found with id: " + id));
    }

    // Utility to transform a PatientReport entity into a response DTO
    private PatientReportDTO mapToDTO(@NonNull PatientReport report) {
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

    // Utility to map a incoming DTO into a persistent PatientReport entity
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
