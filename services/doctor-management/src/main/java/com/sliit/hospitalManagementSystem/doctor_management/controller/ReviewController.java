package com.sliit.hospitalManagementSystem.doctor_management.controller;

import com.sliit.hospitalManagementSystem.doctor_management.dto.ReviewDTO;
import com.sliit.hospitalManagementSystem.doctor_management.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    // Service for handling patient feedback and ratings for doctors
    private final ReviewService reviewService;

    // Submits a new review and rating for a doctor
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ReviewDTO> submitReview(@RequestBody ReviewDTO reviewDto) {
        return new ResponseEntity<>(reviewService.submitReview(reviewDto), HttpStatus.CREATED);
    }

    // Fetches all reviews and feedback associated with a specific doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ReviewDTO>> getDoctorReviews(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getReviewsByDoctor(doctorId));
    }

    // Calculates and returns the average star rating for a specific doctor
    @GetMapping("/doctor/{doctorId}/average")
    public ResponseEntity<Double> getDoctorAverageRating(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getAverageRating(doctorId));
    }

    // Endpoint for administrators to remove inappropriate or duplicate reviews
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
