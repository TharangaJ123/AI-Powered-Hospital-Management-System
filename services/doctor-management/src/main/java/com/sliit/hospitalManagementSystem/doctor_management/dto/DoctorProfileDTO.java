package com.sliit.hospitalManagementSystem.doctor_management.dto;

public class DoctorProfileDTO {

    private Long id;
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
    private String status;
    private Double averageRating;
    private Integer reviewCount;

    public DoctorProfileDTO() {}

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public static DoctorProfileDTOBuilder builder() { return new DoctorProfileDTOBuilder(); }

    public static class DoctorProfileDTOBuilder {
        private Long id;
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
        private String status;
        private Double averageRating;
        private Integer reviewCount;

        public DoctorProfileDTOBuilder id(Long id) { this.id = id; return this; }
        public DoctorProfileDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public DoctorProfileDTOBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public DoctorProfileDTOBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public DoctorProfileDTOBuilder email(String email) { this.email = email; return this; }
        public DoctorProfileDTOBuilder phone(String phone) { this.phone = phone; return this; }
        public DoctorProfileDTOBuilder specialization(String specialization) { this.specialization = specialization; return this; }
        public DoctorProfileDTOBuilder qualification(String qualification) { this.qualification = qualification; return this; }
        public DoctorProfileDTOBuilder experienceYears(Integer experienceYears) { this.experienceYears = experienceYears; return this; }
        public DoctorProfileDTOBuilder licenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; return this; }
        public DoctorProfileDTOBuilder bio(String bio) { this.bio = bio; return this; }
        public DoctorProfileDTOBuilder profilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; return this; }
        public DoctorProfileDTOBuilder consultationFee(Double consultationFee) { this.consultationFee = consultationFee; return this; }
        public DoctorProfileDTOBuilder isAvailableForTelemedicine(Boolean isAvailableForTelemedicine) { this.isAvailableForTelemedicine = isAvailableForTelemedicine; return this; }
        public DoctorProfileDTOBuilder status(String status) { this.status = status; return this; }
        public DoctorProfileDTOBuilder averageRating(Double averageRating) { this.averageRating = averageRating; return this; }
        public DoctorProfileDTOBuilder reviewCount(Integer reviewCount) { this.reviewCount = reviewCount; return this; }

        public DoctorProfileDTO build() {
            DoctorProfileDTO d = new DoctorProfileDTO();
            d.setId(this.id);
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
            d.setAverageRating(this.averageRating);
            d.setReviewCount(this.reviewCount);
            return d;
        }
    }
}
