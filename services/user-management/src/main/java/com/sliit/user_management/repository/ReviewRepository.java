package com.sliit.user_management.repository;

import com.sliit.user_management.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
