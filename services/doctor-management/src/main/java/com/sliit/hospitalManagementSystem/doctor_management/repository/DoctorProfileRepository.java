package com.sliit.hospitalManagementSystem.doctor_management.repository;

import com.sliit.hospitalManagementSystem.doctor_management.model.DoctorProfile;
import com.sliit.hospitalManagementSystem.doctor_management.model.DoctorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {

    Optional<DoctorProfile> findByUserId(Long userId);

    Optional<DoctorProfile> findByEmail(String email);

    Optional<DoctorProfile> findByLicenseNumber(String licenseNumber);

    List<DoctorProfile> findBySpecialization(String specialization);

    List<DoctorProfile> findByStatus(DoctorStatus status);

    List<DoctorProfile> findByIsAvailableForTelemedicine(Boolean isAvailableForTelemedicine);

    List<DoctorProfile> findBySpecializationAndStatus(String specialization, DoctorStatus status);
}
