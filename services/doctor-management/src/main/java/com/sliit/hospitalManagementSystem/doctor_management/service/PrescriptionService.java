package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.PrescriptionDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.Prescription;
import com.sliit.hospitalManagementSystem.doctor_management.repository.PrescriptionRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @SuppressWarnings("null")
    public PrescriptionDTO createPrescription(PrescriptionDTO dto) {
        Prescription prescription = mapToEntity(dto);
        return mapToDTO(prescriptionRepository.save(prescription));
    }

    public PrescriptionDTO getPrescriptionById(@NonNull Long id) {
        return prescriptionRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + id));
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

    public PrescriptionDTO updatePrescription(@NonNull Long id, PrescriptionDTO dto) {
        return prescriptionRepository.findById(id)
                .map(existing -> {
                    existing.setDiagnosis(dto.getDiagnosis());
                    existing.setMedications(dto.getMedications());
                    existing.setDosageInstructions(dto.getDosageInstructions());
                    existing.setAdditionalNotes(dto.getAdditionalNotes());
                    existing.setValidUntil(dto.getValidUntil());
                    return mapToDTO(prescriptionRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + id));
    }

    public void deletePrescription(@NonNull Long id) {
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
