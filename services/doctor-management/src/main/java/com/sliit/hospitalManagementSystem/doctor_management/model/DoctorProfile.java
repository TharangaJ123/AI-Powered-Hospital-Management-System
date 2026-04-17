package com.sliit.hospitalManagementSystem.doctor_management.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_profiles")
public class DoctorProfile {

    // Primary key for the doctor profile
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique user ID linking to the main authentication system
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    // Doctor's legal first name
    @Column(name = "first_name", nullable = false)
    private String firstName;

    // Doctor's legal last name
    @Column(name = "last_name", nullable = false)
    private String lastName;

    // Official professional email address
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Direct contact phone number
    @Column(name = "phone")
    private String phone;

    // Medical area of expertise (e.g., ENT, Orthopedics)
    @Column(name = "specialization", nullable = false)
    private String specialization;

    // Professional degrees and certifications
    @Column(name = "qualification", columnDefinition = "TEXT")
    private String qualification;

    // Total years spent in the medical field
    @Column(name = "experience_years")
    private Integer experienceYears;

    // Official medical license registration number
    @Column(name = "license_number", unique = true)
    private String licenseNumber;

    // Detailed professional summary or bio
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    // Path or URL to the doctor's profile picture
    @Column(name = "profile_photo_url", columnDefinition = "TEXT")
    private String profilePhotoUrl;

    // Cost per consultation session
    @Column(name = "consultation_fee")
    private Double consultationFee;

    // Availability flag for remote video consultations
    @Column(name = "is_available_for_telemedicine")
    private Boolean isAvailableForTelemedicine;

    // Registration and approval state (e.g., PENDING, APPROVED, SUSPENDED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DoctorStatus status;

    // Timestamp when the profile was first created
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Timestamp of the most recent profile update
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DoctorProfile() {}

    // JPA hook to initialize metadata before the first database save
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = DoctorStatus.PENDING_APPROVAL;
        }
        if (isAvailableForTelemedicine == null) {
            isAvailableForTelemedicine = false;
        }
    }

    // JPA hook to refresh the update timestamp before any field modification
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }
    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }
    public Boolean getIsAvailableForTelemedicine() { return isAvailableForTelemedicine; }
    public void setIsAvailableForTelemedicine(Boolean isAvailableForTelemedicine) { this.isAvailableForTelemedicine = isAvailableForTelemedicine; }
    public DoctorStatus getStatus() { return status; }
    public void setStatus(DoctorStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static DoctorProfileBuilder builder() { return new DoctorProfileBuilder(); }

    public static class DoctorProfileBuilder {
        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String specialization;
        private String qualification;
        private Integer experienceYears;
        private String licenseNumber;
        private String bio;
        private String profilePhotoUrl;
        private Double consultationFee;
        private Boolean isAvailableForTelemedicine;
        private DoctorStatus status;

        public DoctorProfileBuilder userId(Long userId) { this.userId = userId; return this; }
        public DoctorProfileBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public DoctorProfileBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public DoctorProfileBuilder email(String email) { this.email = email; return this; }
        public DoctorProfileBuilder phone(String phone) { this.phone = phone; return this; }
        public DoctorProfileBuilder specialization(String specialization) { this.specialization = specialization; return this; }
        public DoctorProfileBuilder qualification(String qualification) { this.qualification = qualification; return this; }
        public DoctorProfileBuilder experienceYears(Integer experienceYears) { this.experienceYears = experienceYears; return this; }
        public DoctorProfileBuilder licenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; return this; }
        public DoctorProfileBuilder bio(String bio) { this.bio = bio; return this; }
        public DoctorProfileBuilder profilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; return this; }
        public DoctorProfileBuilder consultationFee(Double consultationFee) { this.consultationFee = consultationFee; return this; }
        public DoctorProfileBuilder isAvailableForTelemedicine(Boolean isAvailableForTelemedicine) { this.isAvailableForTelemedicine = isAvailableForTelemedicine; return this; }
        public DoctorProfileBuilder status(DoctorStatus status) { this.status = status; return this; }

        public DoctorProfile build() {
            DoctorProfile d = new DoctorProfile();
            d.setUserId(this.userId);
            d.setFirstName(this.firstName);
            d.setLastName(this.lastName);
            d.setEmail(this.email);
            d.setPhone(this.phone);
            d.setSpecialization(this.specialization);
            d.setQualification(this.qualification);
            d.setExperienceYears(this.experienceYears);
            d.setLicenseNumber(this.licenseNumber);
            d.setBio(this.bio);
            d.setProfilePhotoUrl(this.profilePhotoUrl);
            d.setConsultationFee(this.consultationFee);
            d.setIsAvailableForTelemedicine(this.isAvailableForTelemedicine);
            d.setStatus(this.status);
            return d;
        }
    }
}
