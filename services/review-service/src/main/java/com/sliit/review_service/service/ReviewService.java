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

    private final ReviewRepository reviewRepository;

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

    public List<Review> getReviewsForDoctor(Long doctorId) {
        return reviewRepository.findByDoctorIdAndStatus(doctorId, ReviewStatus.APPROVED);
    }

    public List<Review> getReviewsByPatient(Long patientId) {
        return reviewRepository.findByPatientId(patientId);
    }

    public DoctorStatsDto getDoctorStats(Long doctorId) {
        Double avg = reviewRepository.getAverageRatingForDoctor(doctorId);
        Long count = reviewRepository.countApprovedReviewsForDoctor(doctorId);
        return new DoctorStatsDto(doctorId, avg != null ? avg : 0.0, count != null ? count : 0L);
    }

    public Review updateReviewStatus(Long reviewId, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setStatus(status);
        return reviewRepository.save(review);
    }
}
