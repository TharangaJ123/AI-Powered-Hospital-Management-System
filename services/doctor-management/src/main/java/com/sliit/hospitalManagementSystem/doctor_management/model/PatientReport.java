package com.sliit.hospitalManagementSystem.doctor_management.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_reports")
public class PatientReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "report_title", nullable = false)
    private String reportTitle;

    @Column(name = "report_type")
    private String reportType;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "doctor_remarks", columnDefinition = "TEXT")
    private String doctorRemarks;

    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    public PatientReport() {}

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDoctorRemarks() { return doctorRemarks; }
    public void setDoctorRemarks(String doctorRemarks) { this.doctorRemarks = doctorRemarks; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public static PatientReportBuilder builder() { return new PatientReportBuilder(); }

    public static class PatientReportBuilder {
        private Long doctorId;
        private Long patientId;
        private String reportTitle;
        private String reportType;
        private String fileUrl;
        private String fileName;
        private String description;
        private String doctorRemarks;

        public PatientReportBuilder doctorId(Long doctorId) { this.doctorId = doctorId; return this; }
        public PatientReportBuilder patientId(Long patientId) { this.patientId = patientId; return this; }
        public PatientReportBuilder reportTitle(String reportTitle) { this.reportTitle = reportTitle; return this; }
        public PatientReportBuilder reportType(String reportType) { this.reportType = reportType; return this; }
        public PatientReportBuilder fileUrl(String fileUrl) { this.fileUrl = fileUrl; return this; }
        public PatientReportBuilder fileName(String fileName) { this.fileName = fileName; return this; }
        public PatientReportBuilder description(String description) { this.description = description; return this; }
        public PatientReportBuilder doctorRemarks(String doctorRemarks) { this.doctorRemarks = doctorRemarks; return this; }

        public PatientReport build() {
            PatientReport r = new PatientReport();
            r.setDoctorId(this.doctorId);
            r.setPatientId(this.patientId);
            r.setReportTitle(this.reportTitle);
            r.setReportType(this.reportType);
            r.setFileUrl(this.fileUrl);
            r.setFileName(this.fileName);
            r.setDescription(this.description);
            r.setDoctorRemarks(this.doctorRemarks);
            return r;
        }
    }
}
