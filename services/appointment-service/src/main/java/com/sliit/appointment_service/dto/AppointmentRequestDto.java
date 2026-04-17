package com.sliit.appointment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentRequestDto {
    // Unique identifier of the patient making the booking
    private Long patientId;
    // Unique identifier of the doctor being booked
    private Long doctorId;
    // Full name of the patient
    private String fullName;
    // Email address for contact and notifications
    private String email;
    // Contact phone number of the patient
    private String phoneNumber;
    // Scheduled date and time for the appointment
    private LocalDateTime appointmentDate;
    // The mode of consultation (e.g., Online, Physical)
    private String consultationType;
    // Brief explanation for the visit
    private String reason;
    // Additional notes provided by the doctor (optional during booking)
    private String doctorNotes;
    // Name of the doctor (redundant but useful for simplified mapping)
    private String doctorName;
    // Medical specialty of the doctor
    private String specialty;
}
