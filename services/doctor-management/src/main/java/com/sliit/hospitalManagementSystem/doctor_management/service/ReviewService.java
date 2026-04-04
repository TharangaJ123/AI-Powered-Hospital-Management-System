package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.ReviewDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.Review;
import com.sliit.hospitalManagementSystem.doctor_management.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final WebClient.Builder webClientBuilder;

    public ReviewDTO submitReview(ReviewDTO reviewDto) {
        // 1. Verify that the patient has a completed appointment with this doctor
        boolean hasCompletedAppointment = verifyCompletedAppointment(reviewDto.getPatientId(), reviewDto.getDoctorId());
        
        if (!hasCompletedAppointment) {
            throw new RuntimeException("You can only review doctors after a COMPLETED appointment.");
        }

        Review review = Review.builder()
                .doctorId(reviewDto.getDoctorId())
                .patientId(reviewDto.getPatientId())
                .patientName(reviewDto.getPatientName() != null ? reviewDto.getPatientName() : "Anonymous Patient")
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        return mapToDTO(saved);
    }

    private boolean verifyCompletedAppointment(Long patientId, Long doctorId) {
        try {
            // Call appointment-service to check status
            // Note: In local dev with Eureka, this would use http://appointment-service
            // If discovery is not used, we'd use localhost:8081 (default port for appointment service)
            // Using service name for now as per other service calls in codebase
            List<Map<String, Object>> appointments = webClientBuilder.build()
                .get()
                .uri("http://appointment-service/api/appointments/patient/" + patientId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();

            if (appointments == null) return false;

            return appointments.stream()
                .anyMatch(app -> 
                    doctorId.equals(Long.valueOf(app.get("doctorId").toString())) && 
                    "COMPLETED".equals(app.get("status"))
                );
        } catch (Exception e) {
            log.error("Error verifying appointment with appointment-service: {}", e.getMessage());
            // In a real world app, we might fail open or closed based on design. 
            // Here, we'll be strict for security.
            return false;
        }
    }

    public List<ReviewDTO> getReviewsByDoctor(Long doctorId) {
        return reviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Double getAverageRating(Long doctorId) {
        Double avg = reviewRepository.findAverageRatingByDoctorId(doctorId);
        return avg != null ? avg : 0.0;
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    private ReviewDTO mapToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .doctorId(review.getDoctorId())
                .patientId(review.getPatientId())
                .patientName(review.getPatientName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
