package com.sliit.appointment_service.dto;

import com.sliit.appointment_service.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentResponseDto {
    // Unique identifier of the appointment record
    private Long id;
    // ID of the patient associated with the appointment
    private Long patientId;
    // ID of the doctor associated with the appointment
    private Long doctorId;
    // Full name of the patient
    private String fullName;
    // Email address of the patient
    private String email;
    // Contact phone number of the patient
    private String phoneNumber;
    // Confirmed date and time of the appointment
    private LocalDateTime appointmentDate;
    // Current state of the appointment (e.g., SCHEDULED, COMPLETED)
    private AppointmentStatus status;
    // Name of the assigned doctor
    private String doctorName;
    // Medical specialty area of the doctor
    private String specialty;
}
