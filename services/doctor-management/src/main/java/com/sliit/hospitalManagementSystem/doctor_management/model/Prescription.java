package com.sliit.hospitalManagementSystem.doctor_management.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "appointment_request_id")
    private Long appointmentRequestId;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "medications", columnDefinition = "TEXT")
    private String medications;

    @Column(name = "dosage_instructions", columnDefinition = "TEXT")
    private String dosageInstructions;

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "is_digital")
    private Boolean isDigital;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Prescription() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isDigital == null) {
            isDigital = true;
        }
        if (prescriptionDate == null) {
            prescriptionDate = LocalDate.now();
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static PrescriptionBuilder builder() { return new PrescriptionBuilder(); }

    public static class PrescriptionBuilder {
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

        public PrescriptionBuilder doctorId(Long doctorId) { this.doctorId = doctorId; return this; }
        public PrescriptionBuilder patientId(Long patientId) { this.patientId = patientId; return this; }
        public PrescriptionBuilder appointmentRequestId(Long appointmentRequestId) { this.appointmentRequestId = appointmentRequestId; return this; }
        public PrescriptionBuilder diagnosis(String diagnosis) { this.diagnosis = diagnosis; return this; }
        public PrescriptionBuilder medications(String medications) { this.medications = medications; return this; }
        public PrescriptionBuilder dosageInstructions(String dosageInstructions) { this.dosageInstructions = dosageInstructions; return this; }
        public PrescriptionBuilder additionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; return this; }
        public PrescriptionBuilder prescriptionDate(LocalDate prescriptionDate) { this.prescriptionDate = prescriptionDate; return this; }
        public PrescriptionBuilder validUntil(LocalDate validUntil) { this.validUntil = validUntil; return this; }
        public PrescriptionBuilder isDigital(Boolean isDigital) { this.isDigital = isDigital; return this; }

        public Prescription build() {
            Prescription p = new Prescription();
            p.setDoctorId(this.doctorId);
            p.setPatientId(this.patientId);
            p.setAppointmentRequestId(this.appointmentRequestId);
            p.setDiagnosis(this.diagnosis);
            p.setMedications(this.medications);
            p.setDosageInstructions(this.dosageInstructions);
            p.setAdditionalNotes(this.additionalNotes);
            p.setPrescriptionDate(this.prescriptionDate);
            p.setValidUntil(this.validUntil);
            p.setIsDigital(this.isDigital);
            return p;
        }
    }
}
