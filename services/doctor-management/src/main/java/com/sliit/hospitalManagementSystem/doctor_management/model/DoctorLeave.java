package com.sliit.hospitalManagementSystem.doctor_management.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_leaves")
public class DoctorLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaveStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DoctorLeave() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = LeaveStatus.PENDING;
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
