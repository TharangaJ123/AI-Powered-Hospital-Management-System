package com.sliit.user_management.service;

import com.sliit.user_management.dto.ReviewRequestDto;
import com.sliit.user_management.dto.ReviewResponseDto;
import com.sliit.user_management.model.Review;
import com.sliit.user_management.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final WebClient.Builder webClientBuilder;

    private static final String APPOINTMENT_SERVICE_URL = "http://appointment-service/api/appointments/verify-completed";

    /**
     * Submits a new review for a doctor after verifying the patient has a completed appointment.
     */
    public ReviewResponseDto submitReview(Long patientId, ReviewRequestDto request) {
        // Inter-service call to verify completed appointment
        Boolean isCompleted = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/appointments/verify-completed")
                        .queryParam("patientId", patientId)
                        .queryParam("doctorId", request.getDoctorId())
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (isCompleted == null || !isCompleted) {
            throw new RuntimeException("Review submission failed: No completed appointment found with this doctor.");
        }

        Review review = Review.builder()
                .patientId(patientId)
                .doctorId(request.getDoctorId())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        review = reviewRepository.save(review);
        return mapToDto(review);
    }

    /**
     * Retrieves all reviews for a specific doctor.
     */
    public List<ReviewResponseDto> getDoctorReviews(Long doctorId) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getDoctorId().equals(doctorId))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the average rating for a specific doctor.
     */
    public Double getAverageRating(Long doctorId) {
        List<Review> reviews = reviewRepository.findAll().stream()
                .filter(r -> r.getDoctorId().equals(doctorId))
                .toList();

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum / reviews.size();
    }

    /**
     * Deletes a review (Admin only).
     */
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    private ReviewResponseDto mapToDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .doctorId(review.getDoctorId())
                .patientId(review.getPatientId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
