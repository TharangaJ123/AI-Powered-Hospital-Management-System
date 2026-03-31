package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.DoctorProfileDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.DoctorProfile;
import com.sliit.hospitalManagementSystem.doctor_management.model.DoctorStatus;
import com.sliit.hospitalManagementSystem.doctor_management.repository.DoctorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorProfileService {

    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorProfileService(DoctorProfileRepository doctorProfileRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
    }

    public DoctorProfileDTO createProfile(DoctorProfileDTO dto) {
        DoctorProfile profile = mapToEntity(dto);
        DoctorProfile saved = doctorProfileRepository.save(profile);
        return mapToDTO(saved);
    }

    public DoctorProfileDTO getProfileById(Long id) {
        DoctorProfile profile = doctorProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found with id: " + id));
        return mapToDTO(profile);
    }

    public DoctorProfileDTO getProfileByUserId(Long userId) {
        DoctorProfile profile = doctorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found for user id: " + userId));
        return mapToDTO(profile);
    }

    public List<DoctorProfileDTO> getAllProfiles() {
        return doctorProfileRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DoctorProfileDTO> getProfilesBySpecialization(String specialization) {
        return doctorProfileRepository.findBySpecialization(specialization).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DoctorProfileDTO> getProfilesByStatus(String status) {
        DoctorStatus doctorStatus = DoctorStatus.valueOf(status.toUpperCase());
        return doctorProfileRepository.findByStatus(doctorStatus).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DoctorProfileDTO approveDoctor(Long id) {
        DoctorProfile existing = doctorProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found with id: " + id));

        existing.setStatus(DoctorStatus.ACTIVE);
        DoctorProfile updated = doctorProfileRepository.save(existing);
        return mapToDTO(updated);
    }

    public List<DoctorProfileDTO> getTelemedicineDoctors() {
        return doctorProfileRepository.findByIsAvailableForTelemedicine(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DoctorProfileDTO updateProfile(Long id, DoctorProfileDTO dto) {
        DoctorProfile existing = doctorProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found with id: " + id));

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

        DoctorProfile updated = doctorProfileRepository.save(existing);
        return mapToDTO(updated);
    }

    public void deleteProfile(Long id) {
        if (!doctorProfileRepository.existsById(id)) {
            throw new RuntimeException("Doctor profile not found with id: " + id);
        }
        doctorProfileRepository.deleteById(id);
    }

    // --- Mapping helpers ---

    private DoctorProfileDTO mapToDTO(DoctorProfile profile) {
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
