package com.sliit.hospitalManagementSystem.doctor_management.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_requests")
public class AppointmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "requested_date_time", nullable = false)
    private LocalDateTime requestedDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "consultation_type")
    private ConsultationType consultationType;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentRequestStatus status;

    @Column(name = "doctor_notes", columnDefinition = "TEXT")
    private String doctorNotes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public AppointmentRequest() {
    }

    public AppointmentRequest(Long id, Long doctorId, Long patientId, String patientName, LocalDateTime requestedDateTime, ConsultationType consultationType, String reason, AppointmentRequestStatus status, String doctorNotes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.requestedDateTime = requestedDateTime;
        this.consultationType = consultationType;
        this.reason = reason;
        this.status = status;
        this.doctorNotes = doctorNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AppointmentRequestBuilder builder() {
        return new AppointmentRequestBuilder();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = AppointmentRequestStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public LocalDateTime getRequestedDateTime() { return requestedDateTime; }
    public void setRequestedDateTime(LocalDateTime requestedDateTime) { this.requestedDateTime = requestedDateTime; }
    public ConsultationType getConsultationType() { return consultationType; }
    public void setConsultationType(ConsultationType consultationType) { this.consultationType = consultationType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public AppointmentRequestStatus getStatus() { return status; }
    public void setStatus(AppointmentRequestStatus status) { this.status = status; }
    public String getDoctorNotes() { return doctorNotes; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Manual Builder Class
    public static class AppointmentRequestBuilder {
        private Long doctorId;
        private Long patientId;
        private String patientName;
        private LocalDateTime requestedDateTime;
        private ConsultationType consultationType;
        private String reason;
        private AppointmentRequestStatus status;

        public AppointmentRequestBuilder doctorId(Long doctorId) { this.doctorId = doctorId; return this; }
        public AppointmentRequestBuilder patientId(Long patientId) { this.patientId = patientId; return this; }
        public AppointmentRequestBuilder patientName(String patientName) { this.patientName = patientName; return this; }
        public AppointmentRequestBuilder requestedDateTime(LocalDateTime requestedDateTime) { this.requestedDateTime = requestedDateTime; return this; }
        public AppointmentRequestBuilder consultationType(ConsultationType consultationType) { this.consultationType = consultationType; return this; }
        public AppointmentRequestBuilder reason(String reason) { this.reason = reason; return this; }
        public AppointmentRequestBuilder status(AppointmentRequestStatus status) { this.status = status; return this; }

        public AppointmentRequest build() {
            AppointmentRequest req = new AppointmentRequest();
            req.setDoctorId(this.doctorId);
            req.setPatientId(this.patientId);
            req.setPatientName(this.patientName);
            req.setRequestedDateTime(this.requestedDateTime);
            req.setConsultationType(this.consultationType);
            req.setReason(this.reason);
            req.setStatus(this.status);
            return req;
        }
    }
}
