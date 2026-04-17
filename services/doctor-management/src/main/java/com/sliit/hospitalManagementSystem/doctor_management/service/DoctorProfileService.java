package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.DoctorProfileDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.DoctorProfile;
import com.sliit.hospitalManagementSystem.doctor_management.model.DoctorStatus;
import com.sliit.hospitalManagementSystem.doctor_management.repository.DoctorProfileRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorProfileService {

    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorProfileService(DoctorProfileRepository doctorProfileRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
    }

    // Creates a new doctor profile and initializes it for review or immediate activation
    @SuppressWarnings("null")
    public DoctorProfileDTO createProfile(DoctorProfileDTO dto) {
        System.out.println("Creating new profile for userId: " + dto.getUserId() + " with Photo URL: " + dto.getProfilePhotoUrl());
        DoctorProfile profile = mapToEntity(dto);
        return mapToDTO(doctorProfileRepository.save(profile));
    }

    // Fetches a single doctor profile by its internal record ID
    public DoctorProfileDTO getProfileById(@NonNull Long id) {
        return doctorProfileRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found with id: " + id));
    }

    // Retrieves a doctor's profile using the user ID from the central identity system
    @SuppressWarnings("null")
    public DoctorProfileDTO getProfileByUserId(Long userId) {
        DoctorProfile profile = doctorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found for user id: " + userId));
        return mapToDTO(profile);
    }

    // Returns a complete list of all doctor profiles registered in the system
    public List<DoctorProfileDTO> getAllProfiles() {
        return doctorProfileRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Filters profiles by a specific medical area (e.g., Cardiology)
    public List<DoctorProfileDTO> getProfilesBySpecialization(String specialization) {
        return doctorProfileRepository.findBySpecialization(specialization).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Returns profiles filtered by status such as PENDING_APPROVAL or ACTIVE
    public List<DoctorProfileDTO> getProfilesByStatus(String status) {
        DoctorStatus doctorStatus = DoctorStatus.valueOf(status.toUpperCase());
        return doctorProfileRepository.findByStatus(doctorStatus).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Administrative function to officially approve a doctor profile for active service
    public DoctorProfileDTO approveDoctor(@NonNull Long id) {
        return doctorProfileRepository.findById(id)
                .map(existing -> {
                    existing.setStatus(DoctorStatus.ACTIVE);
                    return mapToDTO(doctorProfileRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Doctor profile not found with id: " + id));
    }

    // Returns a list of doctors who have enabled virtual video consultations
    public List<DoctorProfileDTO> getTelemedicineDoctors() {
        return doctorProfileRepository.findByIsAvailableForTelemedicine(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Updates existing profile data including bio, specialization, and contact info
    public DoctorProfileDTO updateProfile(@NonNull Long id, DoctorProfileDTO dto) {
        System.out.println("Updating profile " + id + " with Photo URL: " + dto.getProfilePhotoUrl());
        return doctorProfileRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(dto.getFirstName());
                    existing.setLastName(dto.getLastName());
                    existing.setEmail(dto.getEmail());
                    existing.setPhone(dto.getPhone());
                    existing.setSpecialization(dto.getSpecialization());
                    existing.setQualification(dto.getQualification());
                    existing.setExperienceYears(dto.getExperienceYears());
                    existing.setLicenseNumber(dto.getLicenseNumber());
                    existing.setBio(dto.getBio());
                    existing.setProfilePhotoUrl(dto.getProfilePhotoUrl());
                    existing.setConsultationFee(dto.getConsultationFee());
                    existing.setIsAvailableForTelemedicine(dto.getIsAvailableForTelemedicine());

                    if (dto.getStatus() != null) {
                        existing.setStatus(DoctorStatus.valueOf(dto.getStatus().toUpperCase()));
                    }

                    return mapToDTO(doctorProfileRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Doctor profile not found with id: " + id));
    }

    // Permanently deletes a doctor profile from the repository
    public void deleteProfile(@NonNull Long id) {
        if (!doctorProfileRepository.existsById(id)) {
            throw new RuntimeException("Doctor profile not found with id: " + id);
        }
        doctorProfileRepository.deleteById(id);
    }

    // Converts a DoctorProfile entity into a DTO for front-end or inter-service usage
    private DoctorProfileDTO mapToDTO(@NonNull DoctorProfile profile) {
        return DoctorProfileDTO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .specialization(profile.getSpecialization())
                .qualification(profile.getQualification())
                .experienceYears(profile.getExperienceYears())
                .licenseNumber(profile.getLicenseNumber())
                .bio(profile.getBio())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .consultationFee(profile.getConsultationFee())
                .isAvailableForTelemedicine(profile.getIsAvailableForTelemedicine())
                .status(profile.getStatus() != null ? profile.getStatus().name() : null)
                .build();
    }

    // Maps DTO input back into a JPA entity for database persistence
    private DoctorProfile mapToEntity(DoctorProfileDTO dto) {
        return DoctorProfile.builder()
                .userId(dto.getUserId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .specialization(dto.getSpecialization())
                .qualification(dto.getQualification())
                .experienceYears(dto.getExperienceYears())
                .licenseNumber(dto.getLicenseNumber())
                .bio(dto.getBio())
                .profilePhotoUrl(dto.getProfilePhotoUrl())
                .consultationFee(dto.getConsultationFee())
                .isAvailableForTelemedicine(dto.getIsAvailableForTelemedicine())
                .status(dto.getStatus() != null ? DoctorStatus.valueOf(dto.getStatus().toUpperCase()) : DoctorStatus.ACTIVE)
                .build();
    }
}