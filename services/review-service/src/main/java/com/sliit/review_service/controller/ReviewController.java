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

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewDto reviewDto) {
        return ResponseEntity.ok(reviewService.createReview(reviewDto));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Review>> getReviewsForDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getReviewsForDoctor(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/stats")
    public ResponseEntity<DoctorStatsDto> getDoctorStats(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getDoctorStats(doctorId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Review>> getReviewsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(reviewService.getReviewsByPatient(patientId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Review> updateReviewStatus(@PathVariable Long id, @RequestParam ReviewStatus status) {
        return ResponseEntity.ok(reviewService.updateReviewStatus(id, status));
    }
}
