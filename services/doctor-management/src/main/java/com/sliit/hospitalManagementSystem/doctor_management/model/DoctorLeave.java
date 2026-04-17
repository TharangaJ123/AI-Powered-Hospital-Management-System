package com.sliit.hospitalManagementSystem.doctor_management.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_leaves")
public class DoctorLeave {

    // Primary key for the doctor leave record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key reference to the doctor requesting leave
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    // The first day of the intended leave period
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // The final day of the intended leave period
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // Explanation or justification for the leave request
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    // Current approval status (PENDING, APPROVED, REJECTED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaveStatus status;

    // Timestamp when the leave request was submitted
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Timestamp of the most recent modification to the request status or details
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DoctorLeave() {}

    // JPA lifecycle hook to set initial timestamps and default status before persistence
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = LeaveStatus.PENDING;
        }
    }

    // JPA lifecycle hook to refresh the update timestamp before every database update
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static DoctorLeaveBuilder builder() { return new DoctorLeaveBuilder(); }

    public static class DoctorLeaveBuilder {
        private Long doctorId;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
        private LeaveStatus status;

        public DoctorLeaveBuilder doctorId(Long doctorId) { this.doctorId = doctorId; return this; }
        public DoctorLeaveBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public DoctorLeaveBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public DoctorLeaveBuilder reason(String reason) { this.reason = reason; return this; }
        public DoctorLeaveBuilder status(LeaveStatus status) { this.status = status; return this; }

        public DoctorLeave build() {
            DoctorLeave l = new DoctorLeave();
            l.setDoctorId(this.doctorId);
            l.setStartDate(this.startDate);
            l.setEndDate(this.endDate);
            l.setReason(this.reason);
            l.setStatus(this.status);
            return l;
        }
    }
}
