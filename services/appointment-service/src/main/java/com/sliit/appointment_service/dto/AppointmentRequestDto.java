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
    private Long patientId;
    private Long doctorId;
    private String fullName;
    private String phoneNumber;
    private LocalDateTime appointmentDate;
}
