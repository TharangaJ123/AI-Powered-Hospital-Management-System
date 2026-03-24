package com.sliit.user_management.service;

import com.sliit.user_management.dto.*;
import com.sliit.user_management.model.*;
import com.sliit.user_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicalDocumentRepository medicalDocumentRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final ReviewRepository reviewRepository;

    public List<DoctorProfileDto> getAllDoctors() {
        return doctorProfileRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<DoctorProfileDto> findBySpecialty(String specialty) {
        // Simple filter for now; better to add a query in repository
        return doctorProfileRepository.findAll().stream()
                .filter(d -> d.getSpecialization() != null && d.getSpecialization().equalsIgnoreCase(specialty))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DoctorProfileDto getProfile(Long userId) {
        DoctorProfile doc = doctorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        return mapToDto(doc);
    }

    @Transactional
    public DoctorProfileDto updateProfile(Long userId, DoctorProfileDto request) {
        DoctorProfile doc = doctorProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    return DoctorProfile.builder().user(user)                            .isVerified(false).build();
                });
                
        doc.setFirstName(request.getFirstName());
        doc.setLastName(request.getLastName());
        doc.setSpecialization(request.getSpecialization());
        doc.setPhoneNumber(request.getPhoneNumber());
        doc.setLicenseNumber(request.getLicenseNumber());
        doc.setExperienceYears(request.getExperienceYears());
        if (request.getConsultationFee() != null) {
            doc.setConsultationFee(request.getConsultationFee());
        }
        
        return mapToDto(doctorProfileRepository.save(doc));
    }

    @Transactional
    public DoctorAvailabilityDto addAvailability(Long doctorId, DoctorAvailabilityDto req) {
        DoctorProfile doc = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
                
        DoctorAvailability availability = DoctorAvailability.builder()
                .doctor(doc)
                .availableDate(req.getAvailableDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .isBooked(false)
                .build();
                
        availability = doctorAvailabilityRepository.save(availability);
        return mapToDto(availability);
    }

    public List<DoctorAvailabilityDto> getAvailability(Long doctorId) {
        return doctorAvailabilityRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrescriptionDto issuePrescription(Long doctorId, PrescriptionDto req) {
        PatientProfile patient = patientProfileRepository.findById(req.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        DoctorProfile doc = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Prescription prescription = Prescription.builder()
                .patient(patient)
                .doctorId(doc.getId())
                .doctorName(doc.getFirstName() + " " + doc.getLastName())
                .medication(req.getMedication())
                .dosage(req.getDosage())
                .instructions(req.getInstructions())
                .build();
                
        prescription = prescriptionRepository.save(prescription);
        return mapToDto(prescription);
    }

    public List<MedicalDocumentDto> viewPatientReports(Long patientId) {
        return medicalDocumentRepository.findByPatientId(patientId).stream()
                .map(doc -> MedicalDocumentDto.builder()
                        .id(doc.getId())
                        .patientId(patientId)
                        .documentName(doc.getDocumentName())
                        .documentUrl(doc.getDocumentUrl())
                        .uploadedAt(doc.getUploadedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Submits a new review for a doctor and updates their average rating.
     * 
     * @param reviewDto the review data
     * @return the saved ReviewDto
     */
    @Transactional
    public ReviewDto submitReview(ReviewDto reviewDto) {
        Review review = Review.builder()
                .doctorId(reviewDto.getDoctorId())
                .patientId(reviewDto.getPatientId())
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                .createdAt(java.time.LocalDateTime.now())
                .build();
        
        review = reviewRepository.save(review);
        
        // Update Doctor Profile Rating
        DoctorProfile doc = doctorProfileRepository.findByUserId(reviewDto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        List<Review> reviews = reviewRepository.findByDoctorIdOrderByCreatedAtDesc(reviewDto.getDoctorId());
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        
        doc.setAverageRating(avg);
        doc.setReviewCount(reviews.size());
        doctorProfileRepository.save(doc);
        
        return mapToReviewDto(review);
    }

    /**
     * Retrieves all reviews for a specific doctor.
     * 
     * @param doctorId the doctor's user ID
     * @return a list of ReviewDto
     */
    public List<ReviewDto> getDoctorReviews(Long doctorId) {
        return reviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId).stream()
                .map(this::mapToReviewDto)
                .collect(Collectors.toList());
    }

    private ReviewDto mapToReviewDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .doctorId(review.getDoctorId())
                .patientId(review.getPatientId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private DoctorProfileDto mapToDto(DoctorProfile d) {
        return DoctorProfileDto.builder()
                .id(d.getId())
                .userId(d.getUser().getId())
                .firstName(d.getFirstName())
                .lastName(d.getLastName())
                .name(d.getFirstName() + " " + d.getLastName())
                .specialization(d.getSpecialization())
                .phoneNumber(d.getPhoneNumber())
                .licenseNumber(d.getLicenseNumber())
                .experienceYears(d.getExperienceYears())
                .isVerified(d.isVerified())
                .consultationFee(d.getConsultationFee())
                .averageRating(d.getAverageRating() != null ? d.getAverageRating() : 0.0)
                .reviewCount(d.getReviewCount() != null ? d.getReviewCount() : 0)
                .build();
    }
    
    private DoctorAvailabilityDto mapToDto(DoctorAvailability a) {
        return DoctorAvailabilityDto.builder()
                .id(a.getId())
                .doctorId(a.getDoctor().getId())
                .availableDate(a.getAvailableDate())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .isBooked(a.isBooked())
                .build();
    }

    private PrescriptionDto mapToDto(Prescription p) {
        return PrescriptionDto.builder()
                .id(p.getId())
                .patientId(p.getPatient().getId())
                .doctorId(p.getDoctorId())
                .doctorName(p.getDoctorName())
                .medication(p.getMedication())
                .dosage(p.getDosage())
                .instructions(p.getInstructions())
                .prescribedAt(p.getPrescribedAt())
                .build();
    }
}
