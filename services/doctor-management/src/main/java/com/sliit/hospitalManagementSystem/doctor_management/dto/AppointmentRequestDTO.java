package com.sliit.hospitalManagementSystem.doctor_management.dto;

import java.time.LocalDateTime;

public class AppointmentRequestDTO {

    private Long id;
    private Long doctorId;
    private Long patientId;
    private String patientName;
    private LocalDateTime requestedDateTime;
    private String consultationType;
    private String reason;
    private String status;
    private String doctorNotes;

    public AppointmentRequestDTO() {}

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
    public String getConsultationType() { return consultationType; }
    public void setConsultationType(String consultationType) { this.consultationType = consultationType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDoctorNotes() { return doctorNotes; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }

    public static AppointmentRequestDTOBuilder builder() { return new AppointmentRequestDTOBuilder(); }

    public static class AppointmentRequestDTOBuilder {
        private Long id;
        private Long doctorId;
        private Long patientId;
        private String patientName;
        private LocalDateTime requestedDateTime;
        private String consultationType;
        private String reason;
        private String status;
        private String doctorNotes;

        public AppointmentRequestDTOBuilder id(Long id) { this.id = id; return this; }
        public AppointmentRequestDTOBuilder doctorId(Long doctorId) { this.doctorId = doctorId; return this; }
        public AppointmentRequestDTOBuilder patientId(Long patientId) { this.patientId = patientId; return this; }
        public AppointmentRequestDTOBuilder patientName(String patientName) { this.patientName = patientName; return this; }
        public AppointmentRequestDTOBuilder requestedDateTime(LocalDateTime requestedDateTime) { this.requestedDateTime = requestedDateTime; return this; }
        public AppointmentRequestDTOBuilder consultationType(String consultationType) { this.consultationType = consultationType; return this; }
        public AppointmentRequestDTOBuilder reason(String reason) { this.reason = reason; return this; }
        public AppointmentRequestDTOBuilder status(String status) { this.status = status; return this; }
        public AppointmentRequestDTOBuilder doctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; return this; }

        public AppointmentRequestDTO build() {
            AppointmentRequestDTO d = new AppointmentRequestDTO();
            d.setId(this.id);
            d.setDoctorId(this.doctorId);
            d.setPatientId(this.patientId);
            d.setPatientName(this.patientName);
            d.setRequestedDateTime(this.requestedDateTime);
            d.setConsultationType(this.consultationType);
            d.setReason(this.reason);
            d.setStatus(this.status);
            d.setDoctorNotes(this.doctorNotes);
            return d;
        }
    }
}
