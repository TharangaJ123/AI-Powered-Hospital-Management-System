package com.sliit.hospitalManagementSystem.telemedicine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponseDTO {

    private Long sessionId;
    private String appointmentId;
    private String doctorId;
    private String patientId;
    private String roomName;
    private String sessionUrl;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
