package com.sliit.hospitalManagementSystem.doctor_management.dto;

import java.time.LocalDateTime;

public class PatientReportDTO {

    private Long id;
    private Long doctorId;
    private Long patientId;
    private String reportTitle;
    private String reportType;
    private String fileUrl;
    private String fileName;
    private String description;
    private String doctorRemarks;
    private LocalDateTime uploadedAt;

    public PatientReportDTO() {}

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

    public static PatientReportDTOBuilder builder() { return new PatientReportDTOBuilder(); }

    public static class PatientReportDTOBuilder {
        private Long id;
        private Long doctorId;
        private Long patientId;
        private String reportTitle;
        private String reportType;
        private String fileUrl;
        private String fileName;
        private String description;
        private String doctorRemarks;
        private LocalDateTime uploadedAt;

        public PatientReportDTOBuilder id(Long id) { this.id = id; return this; }
        public PatientReportDTOBuilder doctorId(Long doctorId) { this.doctorId = doctorId; return this; }
        public PatientReportDTOBuilder patientId(Long patientId) { this.patientId = patientId; return this; }
        public PatientReportDTOBuilder reportTitle(String reportTitle) { this.reportTitle = reportTitle; return this; }
        public PatientReportDTOBuilder reportType(String reportType) { this.reportType = reportType; return this; }
        public PatientReportDTOBuilder fileUrl(String fileUrl) { this.fileUrl = fileUrl; return this; }
        public PatientReportDTOBuilder fileName(String fileName) { this.fileName = fileName; return this; }
        public PatientReportDTOBuilder description(String description) { this.description = description; return this; }
        public PatientReportDTOBuilder doctorRemarks(String doctorRemarks) { this.doctorRemarks = doctorRemarks; return this; }
        public PatientReportDTOBuilder uploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; return this; }

        public PatientReportDTO build() {
            PatientReportDTO d = new PatientReportDTO();
            d.setId(this.id);
            d.setDoctorId(this.doctorId);
            d.setPatientId(this.patientId);
            d.setReportTitle(this.reportTitle);
            d.setReportType(this.reportType);
            d.setFileUrl(this.fileUrl);
            d.setFileName(this.fileName);
            d.setDescription(this.description);
            d.setDoctorRemarks(this.doctorRemarks);
            d.setUploadedAt(this.uploadedAt);
            return d;
        }
    }
}
