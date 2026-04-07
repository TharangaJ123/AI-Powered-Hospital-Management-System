package com.sliit.hospitalManagementSystem.doctor_management.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemedicine_sessions")
public class TelemedicineSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "appointment_request_id")
    private Long appointmentRequestId;

    @Column(name = "session_url")
    private String sessionUrl;

    @Column(name = "scheduled_start_time", nullable = false)
    private LocalDateTime scheduledStartTime;

    @Column(name = "scheduled_end_time")
    private LocalDateTime scheduledEndTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public TelemedicineSession() {}

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
