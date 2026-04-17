package com.sliit.hospitalManagementSystem.doctor_management.repository;

import com.sliit.hospitalManagementSystem.doctor_management.model.DoctorProfile;
import com.sliit.hospitalManagementSystem.doctor_management.model.DoctorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {

    // Finds a doctor profile linked to a specific system user ID
    Optional<DoctorProfile> findByUserId(Long userId);

    // Look up a doctor profile by their professional email address
    Optional<DoctorProfile> findByEmail(String email);

    // Verifies a doctor's record using their unique medical license number
    Optional<DoctorProfile> findByLicenseNumber(String licenseNumber);

    // Retrieves all doctors belonging to a specific medical field
    List<DoctorProfile> findBySpecialization(String specialization);

    // Filters doctors based on their registration status (e.g., ACTIVE, PENDING)
    List<DoctorProfile> findByStatus(DoctorStatus status);

    // Retrieves all doctors who offer or do not offer virtual consultations
    List<DoctorProfile> findByIsAvailableForTelemedicine(Boolean isAvailableForTelemedicine);

    // Advanced filter to find active doctors within a specific specialization
    List<DoctorProfile> findBySpecializationAndStatus(String specialization, DoctorStatus status);
}
