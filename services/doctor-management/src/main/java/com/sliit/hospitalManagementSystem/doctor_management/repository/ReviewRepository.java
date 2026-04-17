package com.sliit.hospitalManagementSystem.doctor_management.repository;

import com.sliit.hospitalManagementSystem.doctor_management.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Retrieves all patient reviews for a doctor, starting with the newest
    List<Review> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);

    // Custom JPQL query to calculate the mathematical mean of all ratings for a specific doctor
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.doctorId = :doctorId")
    Double findAverageRatingByDoctorId(Long doctorId);

    // Retrieves all reviews submitted by a specific patient across all doctors
    List<Review> findByPatientId(Long patientId);
}
