package com.sliit.user_management.controller;

import com.sliit.user_management.dto.ReviewRequestDto;
import com.sliit.user_management.dto.ReviewResponseDto;
import com.sliit.user_management.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Submit a new review for a doctor.
     * Only authenticated patients can submit reviews.
     */
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ReviewResponseDto> submitReview(@RequestParam Long patientId, @RequestBody ReviewRequestDto request) {
        return ResponseEntity.ok(reviewService.submitReview(patientId, request));
    }

    /**
     * Get all reviews for a specific doctor.
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ReviewResponseDto>> getDoctorReviews(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getDoctorReviews(doctorId));
    }

    /**
     * Get the average rating for a specific doctor.
     */
    @GetMapping("/doctor/{doctorId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getAverageRating(doctorId));
    }

    /**
     * Delete a review (Admin only).
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
