package com.sliit.user_management.repository;

import com.sliit.user_management.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    Optional<DoctorProfile> findByUserId(Long userId);
    List<DoctorProfile> findBySpecializationContainingIgnoreCase(String specialization);
    long countByIsVerified(boolean isVerified);
}
