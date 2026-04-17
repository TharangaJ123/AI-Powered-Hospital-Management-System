package com.sliit.user_management.service;

import com.sliit.user_management.dto.PatientProfileDto;
import com.sliit.user_management.dto.UserRegistrationDto;
import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.dto.MedicalDocumentDto;
import com.sliit.user_management.dto.MedicalHistoryDto;
import com.sliit.user_management.dto.PrescriptionDto;
import com.sliit.user_management.model.*;
import com.sliit.user_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class handling the business logic for patient-related operations.
 * This includes patient registration, profile updates, and document management.
 */
@Service
@RequiredArgsConstructor
public class PatientService {

        private final UserRepository userRepository;
        private final MedicalDocumentRepository medicalDocumentRepository;
        private final MedicalHistoryRepository medicalHistoryRepository;
        private final PrescriptionRepository prescriptionRepository;
        private final PasswordEncoder passwordEncoder;

        /**
         * Registers a new patient into the system by creating a user and a linked
         * profile.
         * 
         * @param request the registration details provided by the patient
         * @return the created UserResponseDto
         * @throws RuntimeException if the provided email is already taken
         */
        @Transactional
        public UserResponseDto registerPatient(UserRegistrationDto request) {
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                        throw new RuntimeException("Email already taken");
                }

                Patient patient = Patient.builder()
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.PATIENT)
                                .active(true)
                                .isVerified(true) // Patients are auto-verified in this simplified flow
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .phoneNumber(request.getPhoneNumber())
                                .address(request.getAddress())
                                .dateOfBirth(request.getDateOfBirth())
                                .build();

                patient = userRepository.save(patient);

                return UserResponseDto.builder()
                                .id(patient.getId())
                                .email(patient.getEmail())
                                .role(patient.getRole())
                                .active(patient.isActive())
                                .firstName(patient.getFirstName())
                                .lastName(patient.getLastName())
                                .phoneNumber(patient.getPhoneNumber())
                                .build();
        }

        public PatientProfileDto getProfile(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (!(user instanceof Patient)) {
                        throw new RuntimeException("Specified user is not a patient");
                }

                return mapToDto((Patient) user);
        }

        @Transactional
        public PatientProfileDto updateProfile(Long userId, PatientProfileDto request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (!(user instanceof Patient)) {
                        throw new RuntimeException("Specified user is not a patient");
                }

                Patient patient = (Patient) user;
                patient.setFirstName(request.getFirstName());
                patient.setLastName(request.getLastName());
                patient.setPhoneNumber(request.getPhoneNumber());
                patient.setAddress(request.getAddress());
                patient.setDateOfBirth(request.getDateOfBirth());

                return mapToDto(userRepository.save(patient));
        }

        /**
         * Attaches and uploads a new medical document for a patient.
         * 
         * @param patientId the profile ID of the patient
         * @param request   the medical document information (name and URL)
         * @return the saved MedicalDocumentDto
         * @throws RuntimeException if the patient profile cannot be found
         */
        @Transactional
        public MedicalDocumentDto uploadDocument(Long patientId, MedicalDocumentDto request) {
                User user = userRepository.findById(patientId)
                                .orElseThrow(() -> new RuntimeException("Patient not found"));

                if (!(user instanceof Patient)) {
                        throw new RuntimeException("User is not a patient");
                }

                MedicalDocument doc = MedicalDocument.builder()
                                .patient((Patient) user)
                                .documentName(request.getDocumentName())
                                .documentUrl(request.getDocumentUrl())
                                .build();

                doc = medicalDocumentRepository.save(doc);

                return MedicalDocumentDto.builder()
                                .id(doc.getId())
                                .patientId(user.getId())
                                .documentName(doc.getDocumentName())
                                .documentUrl(doc.getDocumentUrl())
                                .uploadedAt(doc.getUploadedAt())
                                .build();
        }

        /**
         * Retrieves all medical documents associated with a specific patient.
         * 
         * @param patientId the profile ID of the patient
         * @return a list of MedicalDocumentDto records belonging to the patient
         */
        public List<MedicalDocumentDto> getPatientDocuments(Long patientId) {
                return medicalDocumentRepository.findByPatientId(patientId).stream()
                                .map(doc -> MedicalDocumentDto.builder()
                                                .id(doc.getId())
                                                .patientId(patientId)
                                                .documentName(doc.getDocumentName())
                                                .documentUrl(doc.getDocumentUrl())
                                                .uploadedAt(doc.getUploadedAt())
                                                .build())
                                .collect(Collectors.toList());
        }

        /**
         * Retrieves all medical history records for a specific patient.
         * 
         * @param patientId the ID of the patient
         * @return a list of MedicalHistoryDto records
         */
        public List<MedicalHistoryDto> getPatientMedicalHistory(Long patientId) {
                return medicalHistoryRepository.findByPatientId(patientId).stream()
                                .map(history -> MedicalHistoryDto.builder()
                                                .id(history.getId())
                                                .patientId(patientId)
                                                .conditionName(history.getConditionName())
                                                .diagnosis(history.getDiagnosis())
                                                .treatment(history.getTreatment())
                                                .recordedDate(history.getRecordedDate())
                                                .build())
                                .collect(Collectors.toList());
        }

        /**
         * Retrieves all prescriptions issued for a specific patient.
         * 
         * @param patientId the ID of the patient
         * @return a list of PrescriptionDto records
         */
        public List<PrescriptionDto> getPatientPrescriptions(Long patientId) {
                return prescriptionRepository.findByPatientId(patientId).stream()
                                .map(prescription -> PrescriptionDto.builder()
                                                .id(prescription.getId())
                                                .patientId(patientId)
                                                .doctorId(prescription.getDoctorId())
                                                .medication(prescription.getMedication())
                                                .dosage(prescription.getDosage())
                                                .instructions(prescription.getInstructions())
                                                .prescribedDate(prescription.getPrescribedDate())
                                                .build())
                                .collect(Collectors.toList());
        }

        private PatientProfileDto mapToDto(Patient p) {
                return PatientProfileDto.builder()
                                .id(p.getId())
                                .userId(p.getId())
                                .firstName(p.getFirstName())
                                .lastName(p.getLastName())
                                .phoneNumber(p.getPhoneNumber())
                                .address(p.getAddress())
                                .dateOfBirth(p.getDateOfBirth())
                                .build();
        }
}