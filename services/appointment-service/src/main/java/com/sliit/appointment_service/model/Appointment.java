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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "full_name", nullable = true, length = 120)
    private String fullName;

    @Column(name = "phone_number", nullable = true, length = 20)
    private String phoneNumber;


    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status;

    @Column(name = "consultation_type", length = 100)
    private String consultationType;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "doctor_notes", columnDefinition = "TEXT")
    private String doctorNotes;
}
