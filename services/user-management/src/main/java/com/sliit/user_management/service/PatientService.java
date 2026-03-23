package com.sliit.user_management.service;

import com.sliit.user_management.dto.*;
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
    private final PatientProfileRepository patientProfileRepository;
    private final MedicalDocumentRepository medicalDocumentRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new patient into the system by creating a user and a linked profile.
     * 
     * @param request the registration details provided by the patient
     * @return the created UserResponseDto
     * @throws RuntimeException if the provided email is already taken
     */
    @Transactional
    public UserResponseDto registerPatient(UserRegistrationDto request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PATIENT)
                .active(true)
                .build();
        user = userRepository.save(user);

        PatientProfile profile = PatientProfile.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        patientProfileRepository.save(profile);

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }

    /**
     * Retrieves the profile details of a patient using their associated user ID.
     * 
     * @param userId the ID of the user linked to the patient profile
     * @return the retrieved PatientProfileDto
     * @throws RuntimeException if the profile cannot be found
     */
    public PatientProfileDto getProfile(Long userId) {
        PatientProfile profile = patientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return mapToDto(profile);
    }

    /**
     * Updates an existing patient profile with newly provided details.
     * 
     * @param userId the ID of the user linked to the patient profile
     * @param request the updated profile details
     * @return the updated PatientProfileDto
     * @throws RuntimeException if the profile cannot be found
     */
    @Transactional
    public PatientProfileDto updateProfile(Long userId, PatientProfileDto request) {
        PatientProfile profile = patientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
                
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAddress(request.getAddress());
        profile.setDateOfBirth(request.getDateOfBirth());
        
        return mapToDto(patientProfileRepository.save(profile));
    }

    /**
     * Attaches and uploads a new medical document for a patient.
     * 
     * @param patientId the profile ID of the patient
     * @param request the medical document information (name and URL)
     * @return the saved MedicalDocumentDto
     * @throws RuntimeException if the patient profile cannot be found
     */
    @Transactional
    public MedicalDocumentDto uploadDocument(Long patientId, MedicalDocumentDto request) {
        PatientProfile profile = patientProfileRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
                
        MedicalDocument doc = MedicalDocument.builder()
                .patient(profile)
                .documentName(request.getDocumentName())
                .documentUrl(request.getDocumentUrl())
                .build();
                
        doc = medicalDocumentRepository.save(doc);
        
        return MedicalDocumentDto.builder()
                .id(doc.getId())
                .patientId(profile.getId())
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
     * Retrieves the medical history records for a specified patient.
     * 
     * @param patientId the profile ID of the patient
     * @return a list of medical history log entries as strings
     */
    public List<String> getMedicalHistory(Long patientId) {
        // Mock medical history for demo
        return List.of(
            "2025-01-10: Diagnosed with mild fever.",
            "2025-02-15: Prescribed Paracetamol 500mg.",
            "2026-03-21: Routine checkup completed."
        );
    }

    private PatientProfileDto mapToDto(PatientProfile p) {
        return PatientProfileDto.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .phoneNumber(p.getPhoneNumber())
                .address(p.getAddress())
                .dateOfBirth(p.getDateOfBirth())
                .build();
    }
}
