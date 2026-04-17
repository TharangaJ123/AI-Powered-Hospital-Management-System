package com.sliit.review_service.repository;

import com.sliit.review_service.entity.Review;
import com.sliit.review_service.entity.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Custom query to find all reviews for a doctor with a specific status (e.g., APPROVED)
    List<Review> findByDoctorIdAndStatus(Long doctorId, ReviewStatus status);

    // Custom query to find all reviews submitted by a specific patient
    List<Review> findByPatientId(Long patientId);
    
    // JPQL query to calculate the average rating for a doctor based on their approved reviews
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.doctorId = :doctorId AND r.status = 'APPROVED'")
    Double getAverageRatingForDoctor(@Param("doctorId") Long doctorId);
    
    // JPQL query to count the total number of approved reviews for a specific doctor
    @Query("SELECT COUNT(r) FROM Review r WHERE r.doctorId = :doctorId AND r.status = 'APPROVED'")
    Long countApprovedReviewsForDoctor(@Param("doctorId") Long doctorId);
}
