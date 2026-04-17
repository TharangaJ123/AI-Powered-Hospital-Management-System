package com.sliit.hospitalManagementSystem.doctor_management.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemedicine_sessions")
public class TelemedicineSession {

    // Primary key for the telemedicine session record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key reference to the participating doctor
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    // Foreign key reference to the participating patient
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    // Link to the associated appointment request that initiated this session
    @Column(name = "appointment_request_id")
    private Long appointmentRequestId;

    // URL link for the video conference (Zoom, Meet, etc.)
    @Column(name = "session_url")
    private String sessionUrl;

    // Planned start time for the virtual consultation
    @Column(name = "scheduled_start_time", nullable = false)
    private LocalDateTime scheduledStartTime;

    // Planned end time for the virtual consultation
    @Column(name = "scheduled_end_time")
    private LocalDateTime scheduledEndTime;

    // Actual timestamp when the doctor joined or started the session
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    // Actual timestamp when the session was officially concluded
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    // Current state of the session lifecycle (e.g., SCHEDULED, ACTIVE)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status;

    // Doctor's clinical or summary notes from the virtual meeting
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Internal timestamp recording when the session record was created
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public TelemedicineSession() {}

    // JPA hook to initialize default values and timestamps before saving
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = SessionStatus.SCHEDULED;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getAppointmentRequestId() { return appointmentRequestId; }
    public void setAppointmentRequestId(Long appointmentRequestId) { this.appointmentRequestId = appointmentRequestId; }
    public String getSessionUrl() { return sessionUrl; }
    public void setSessionUrl(String sessionUrl) { this.sessionUrl = sessionUrl; }
    public LocalDateTime getScheduledStartTime() { return scheduledStartTime; }
    public void setScheduledStartTime(LocalDateTime scheduledStartTime) { this.scheduledStartTime = scheduledStartTime; }
    public LocalDateTime getScheduledEndTime() { return scheduledEndTime; }
    public void setScheduledEndTime(LocalDateTime scheduledEndTime) { this.scheduledEndTime = scheduledEndTime; }
    public LocalDateTime getActualStartTime() { return actualStartTime; }
    public void setActualStartTime(LocalDateTime actualStartTime) { this.actualStartTime = actualStartTime; }
    public LocalDateTime getActualEndTime() { return actualEndTime; }
    public void setActualEndTime(LocalDateTime actualEndTime) { this.actualEndTime = actualEndTime; }
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static TelemedicineSessionBuilder builder() { return new TelemedicineSessionBuilder(); }

    public static class TelemedicineSessionBuilder {
        private Long doctorId;
        private Long patientId;
        private Long appointmentRequestId;
        private String sessionUrl;
        private LocalDateTime scheduledStartTime;
        private LocalDateTime scheduledEndTime;
        private SessionStatus status;

        public TelemedicineSessionBuilder doctorId(Long doctorId) { this.doctorId = doctorId; return this; }
        public TelemedicineSessionBuilder patientId(Long patientId) { this.patientId = patientId; return this; }
        public TelemedicineSessionBuilder appointmentRequestId(Long appointmentRequestId) { this.appointmentRequestId = appointmentRequestId; return this; }
        public TelemedicineSessionBuilder sessionUrl(String sessionUrl) { this.sessionUrl = sessionUrl; return this; }
        public TelemedicineSessionBuilder scheduledStartTime(LocalDateTime scheduledStartTime) { this.scheduledStartTime = scheduledStartTime; return this; }
        public TelemedicineSessionBuilder scheduledEndTime(LocalDateTime scheduledEndTime) { this.scheduledEndTime = scheduledEndTime; return this; }
        public TelemedicineSessionBuilder status(SessionStatus status) { this.status = status; return this; }

        public TelemedicineSession build() {
            TelemedicineSession s = new TelemedicineSession();
            s.setDoctorId(this.doctorId);
            s.setPatientId(this.patientId);
            s.setAppointmentRequestId(this.appointmentRequestId);
            s.setSessionUrl(this.sessionUrl);
            s.setScheduledStartTime(this.scheduledStartTime);
            s.setScheduledEndTime(this.scheduledEndTime);
            s.setStatus(this.status);
            return s;
        }
    }
}
