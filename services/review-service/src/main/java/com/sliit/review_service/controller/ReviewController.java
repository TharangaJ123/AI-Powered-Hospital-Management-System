package com.sliit.review_service.controller;

import com.sliit.review_service.dto.DoctorStatsDto;
import com.sliit.review_service.dto.ReviewDto;
import com.sliit.review_service.entity.Review;
import com.sliit.review_service.entity.ReviewStatus;
import com.sliit.review_service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    // Injecting ReviewService to handle business logic
    private final ReviewService reviewService;

    // Endpoint to create a new review for a doctor
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewDto reviewDto) {
        return ResponseEntity.ok(reviewService.createReview(reviewDto));
    }

    // Endpoint to retrieve all reviews for a specific doctor by their ID
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Review>> getReviewsForDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getReviewsForDoctor(doctorId));
    }

    // Endpoint to get statistical data (e.g., average rating) for a specific doctor
    @GetMapping("/doctor/{doctorId}/stats")
    public ResponseEntity<DoctorStatsDto> getDoctorStats(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getDoctorStats(doctorId));
    }

    // Endpoint to fetch all reviews submitted by a specific patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Review>> getReviewsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(reviewService.getReviewsByPatient(patientId));
    }

    // Endpoint to retrieve all reviews available in the system
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // Endpoint to update the status (e.g., PENDING, APPROVED) of a specific review
    @PutMapping("/{id}/status")
    public ResponseEntity<Review> updateReviewStatus(@PathVariable Long id, @RequestParam ReviewStatus status) {
        return ResponseEntity.ok(reviewService.updateReviewStatus(id, status));
    }
}

