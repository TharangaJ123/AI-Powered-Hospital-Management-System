package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.PrescriptionDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.Prescription;
import com.sliit.hospitalManagementSystem.doctor_management.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public PrescriptionDTO createPrescription(PrescriptionDTO dto) {
        Prescription prescription = mapToEntity(dto);
        Prescription saved = prescriptionRepository.save(prescription);
        return mapToDTO(saved);
    }

    public PrescriptionDTO getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + id));
        return mapToDTO(prescription);
    }

    public List<PrescriptionDTO> getPrescriptionsByDoctorId(Long doctorId) {
        return prescriptionRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PrescriptionDTO> getPrescriptionsByPatientId(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PrescriptionDTO> getPrescriptionsByDoctorAndPatient(Long doctorId, Long patientId) {
        return prescriptionRepository.findByDoctorIdAndPatientId(doctorId, patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PrescriptionDTO updatePrescription(Long id, PrescriptionDTO dto) {
        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + id));

        existing.setDiagnosis(dto.getDiagnosis());
        existing.setMedications(dto.getMedications());
        existing.setDosageInstructions(dto.getDosageInstructions());
        existing.setAdditionalNotes(dto.getAdditionalNotes());
        existing.setValidUntil(dto.getValidUntil());

        Prescription updated = prescriptionRepository.save(existing);
        return mapToDTO(updated);
    }

    public void deletePrescription(Long id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new RuntimeException("Prescription not found with id: " + id);
        }
        prescriptionRepository.deleteById(id);
    }

    // --- Mapping helpers ---

    private PrescriptionDTO mapToDTO(Prescription prescription) {
        return PrescriptionDTO.builder()
                .id(prescription.getId())
                .doctorId(prescription.getDoctorId())
                .patientId(prescription.getPatientId())
                .appointmentRequestId(prescription.getAppointmentRequestId())
                .diagnosis(prescription.getDiagnosis())
                .medications(prescription.getMedications())
                .dosageInstructions(prescription.getDosageInstructions())
                .additionalNotes(prescription.getAdditionalNotes())
                .prescriptionDate(prescription.getPrescriptionDate())
                .validUntil(prescription.getValidUntil())
                .isDigital(prescription.getIsDigital())
                .build();
    }

    private Prescription mapToEntity(PrescriptionDTO dto) {
        return Prescription.builder()
                .doctorId(dto.getDoctorId())
                .patientId(dto.getPatientId())
                .appointmentRequestId(dto.getAppointmentRequestId())
                .diagnosis(dto.getDiagnosis())
                .medications(dto.getMedications())
                .dosageInstructions(dto.getDosageInstructions())
                .additionalNotes(dto.getAdditionalNotes())
                .prescriptionDate(dto.getPrescriptionDate())
                .validUntil(dto.getValidUntil())
                .isDigital(dto.getIsDigital())
                .build();
    }
}
