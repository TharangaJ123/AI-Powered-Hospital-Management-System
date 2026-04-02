package com.sliit.hospitalManagementSystem.doctor_management.dto;

import java.time.LocalDate;

public class PrescriptionDTO {

    private Long id;
    private Long doctorId;
    private Long patientId;
    private Long appointmentRequestId;
    private String diagnosis;
    private String medications;
    private String dosageInstructions;
    private String additionalNotes;
    private LocalDate prescriptionDate;
    private LocalDate validUntil;
    private Boolean isDigital;

    public PrescriptionDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getAppointmentRequestId() { return appointmentRequestId; }
    public void setAppointmentRequestId(Long appointmentRequestId) { this.appointmentRequestId = appointmentRequestId; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getMedications() { return medications; }
    public void setMedications(String medications) { this.medications = medications; }
    public String getDosageInstructions() { return dosageInstructions; }
    public void setDosageInstructions(String dosageInstructions) { this.dosageInstructions = dosageInstructions; }
    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
    public LocalDate getPrescriptionDate() { return prescriptionDate; }
    public void setPrescriptionDate(LocalDate prescriptionDate) { this.prescriptionDate = prescriptionDate; }
    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
    public Boolean getIsDigital() { return isDigital; }
    public void setIsDigital(Boolean isDigital) { this.isDigital = isDigital; }

    public static PrescriptionDTOBuilder builder() { return new PrescriptionDTOBuilder(); }

    public static class PrescriptionDTOBuilder {
        private Long id;
        private Long doctorId;
        private Long patientId;
        private Long appointmentRequestId;
        private String diagnosis;
        private String medications;
        private String dosageInstructions;
        private String additionalNotes;
        private LocalDate prescriptionDate;
        private LocalDate validUntil;
        private Boolean isDigital;

        public PrescriptionDTOBuilder id(Long id) { this.id = id; return this; }
        public PrescriptionDTOBuilder doctorId(Long doctorId) { this.doctorId = doctorId; return this; }
        public PrescriptionDTOBuilder patientId(Long patientId) { this.patientId = patientId; return this; }
        public PrescriptionDTOBuilder appointmentRequestId(Long appointmentRequestId) { this.appointmentRequestId = appointmentRequestId; return this; }
        public PrescriptionDTOBuilder diagnosis(String diagnosis) { this.diagnosis = diagnosis; return this; }
        public PrescriptionDTOBuilder medications(String medications) { this.medications = medications; return this; }
        public PrescriptionDTOBuilder dosageInstructions(String dosageInstructions) { this.dosageInstructions = dosageInstructions; return this; }
        public PrescriptionDTOBuilder additionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; return this; }
        public PrescriptionDTOBuilder prescriptionDate(LocalDate prescriptionDate) { this.prescriptionDate = prescriptionDate; return this; }
        public PrescriptionDTOBuilder validUntil(LocalDate validUntil) { this.validUntil = validUntil; return this; }
        public PrescriptionDTOBuilder isDigital(Boolean isDigital) { this.isDigital = isDigital; return this; }

        public PrescriptionDTO build() {
            PrescriptionDTO d = new PrescriptionDTO();
            d.setId(this.id);
            d.setDoctorId(this.doctorId);
            d.setPatientId(this.patientId);
            d.setAppointmentRequestId(this.appointmentRequestId);
            d.setDiagnosis(this.diagnosis);
            d.setMedications(this.medications);
            d.setDosageInstructions(this.dosageInstructions);
            d.setAdditionalNotes(this.additionalNotes);
            d.setPrescriptionDate(this.prescriptionDate);
            d.setValidUntil(this.validUntil);
            d.setIsDigital(this.isDigital);
            return d;
        }
    }
}
