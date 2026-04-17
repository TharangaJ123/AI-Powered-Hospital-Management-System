package com.sliit.review_service.service;

import com.sliit.review_service.dto.DoctorStatsDto;
import com.sliit.review_service.dto.ReviewDto;
import com.sliit.review_service.entity.Review;
import com.sliit.review_service.entity.ReviewStatus;
import com.sliit.review_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    // Injecting ReviewRepository to interact with the database
    private final ReviewRepository reviewRepository;

    // Logic to create and save a new review based on the provided DTO
    public Review createReview(ReviewDto dto) {
        Review review = Review.builder()
                .doctorId(dto.getDoctorId())
                .patientId(dto.getPatientId())
                .appointmentId(dto.getAppointmentId())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .status(ReviewStatus.APPROVED) // Auto approve for now
                .build();
        return reviewRepository.save(review);
    }

    // Fetches all approved reviews for a specific doctor
    public List<Review> getReviewsForDoctor(Long doctorId) {
        return reviewRepository.findByDoctorIdAndStatus(doctorId, ReviewStatus.APPROVED);
    }

    // Fetches all reviews submitted by a specific patient
    public List<Review> getReviewsByPatient(Long patientId) {
        return reviewRepository.findByPatientId(patientId);
    }

    // Calculates and returns statistical data for a doctor (avg rating and review count)
    public DoctorStatsDto getDoctorStats(Long doctorId) {
        Double avg = reviewRepository.getAverageRatingForDoctor(doctorId);
        Long count = reviewRepository.countApprovedReviewsForDoctor(doctorId);
        return new DoctorStatsDto(doctorId, avg != null ? avg : 0.0, count != null ? count : 0L);
    }

    // Updates the status of an existing review after verifying its existence
    public Review updateReviewStatus(Long reviewId, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setStatus(status);
        return reviewRepository.save(review);
    }

    // Retrieves every review currently stored in the database
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}

