package com.sliit.appointment_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    // Primary key for the appointment record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key reference to the patient
    @Column(name = "patient_id")
    private Long patientId;

    // Foreign key reference to the doctor
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    // The patient's full name recorded at the time of booking
    @Column(name = "full_name", nullable = true, length = 120)
    private String fullName;

    // The patient's email address recorded at the time of booking
    @Column(name = "email", nullable = true, length = 120)
    private String email;

    // The patient's phone number recorded at the time of booking
    @Column(name = "phone_number", nullable = true, length = 20)
    private String phoneNumber;

    // The scheduled timestamp for the medical consultation
    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    // The current status of the appointment (SCHEDULED, COMPLETED, etc.)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status;

    // The mode of the meeting (e.g., Online or Physical)
    @Column(name = "consultation_type", length = 100)
    private String consultationType;

    // A brief description of the medical issue or reason for the visit
    @Column(name = "reason", length = 255)
    private String reason;

    // Detailed medical notes added by the doctor during or after the session
    @Column(name = "doctor_notes", columnDefinition = "TEXT")
    private String doctorNotes;

    // The name of the assigned doctor (cached for quick retrieval)
    @Column(name = "doctor_name", length = 120)
    private String doctorName;

    // The medical specialty of the assigned doctor
    @Column(name = "specialty", length = 100)
    private String specialty;
}
