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
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String fullName;
    private String phoneNumber;
    private String doctorSummary;
    private String patientNotes;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
}
