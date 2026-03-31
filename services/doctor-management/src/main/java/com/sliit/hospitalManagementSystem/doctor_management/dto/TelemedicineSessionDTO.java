package com.sliit.hospitalManagementSystem.doctor_management.dto;

import java.time.LocalDateTime;

public class TelemedicineSessionDTO {

    private Long id;
    private Long doctorId;
    private Long patientId;
    private Long appointmentRequestId;
    private String sessionUrl;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private String status;
    private String notes;

    public TelemedicineSessionDTO() {}

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static TelemedicineSessionDTOBuilder builder() { return new TelemedicineSessionDTOBuilder(); }

    public static class TelemedicineSessionDTOBuilder {
        private Long id;
        private Long doctorId;
        private Long patientId;
        private Long appointmentRequestId;
        private String sessionUrl;
        private LocalDateTime scheduledStartTime;
        private LocalDateTime scheduledEndTime;
        private LocalDateTime actualStartTime;
        private LocalDateTime actualEndTime;
        private String status;
        private String notes;

        public TelemedicineSessionDTOBuilder id(Long id) { this.id = id; return this; }
        public TelemedicineSessionDTOBuilder doctorId(Long doctorId) { this.doctorId = doctorId; return this; }
        public TelemedicineSessionDTOBuilder patientId(Long patientId) { this.patientId = patientId; return this; }
        public TelemedicineSessionDTOBuilder appointmentRequestId(Long appointmentRequestId) { this.appointmentRequestId = appointmentRequestId; return this; }
        public TelemedicineSessionDTOBuilder sessionUrl(String sessionUrl) { this.sessionUrl = sessionUrl; return this; }
        public TelemedicineSessionDTOBuilder scheduledStartTime(LocalDateTime scheduledStartTime) { this.scheduledStartTime = scheduledStartTime; return this; }
        public TelemedicineSessionDTOBuilder scheduledEndTime(LocalDateTime scheduledEndTime) { this.scheduledEndTime = scheduledEndTime; return this; }
        public TelemedicineSessionDTOBuilder actualStartTime(LocalDateTime actualStartTime) { this.actualStartTime = actualStartTime; return this; }
        public TelemedicineSessionDTOBuilder actualEndTime(LocalDateTime actualEndTime) { this.actualEndTime = actualEndTime; return this; }
        public TelemedicineSessionDTOBuilder status(String status) { this.status = status; return this; }
        public TelemedicineSessionDTOBuilder notes(String notes) { this.notes = notes; return this; }

        public TelemedicineSessionDTO build() {
            TelemedicineSessionDTO d = new TelemedicineSessionDTO();
            d.setId(this.id);
            d.setDoctorId(this.doctorId);
            d.setPatientId(this.patientId);
            d.setAppointmentRequestId(this.appointmentRequestId);
            d.setSessionUrl(this.sessionUrl);
            d.setScheduledStartTime(this.scheduledStartTime);
            d.setScheduledEndTime(this.scheduledEndTime);
            d.setActualStartTime(this.actualStartTime);
            d.setActualEndTime(this.actualEndTime);
            d.setStatus(this.status);
            d.setNotes(this.notes);
            return d;
        }
    }
}
