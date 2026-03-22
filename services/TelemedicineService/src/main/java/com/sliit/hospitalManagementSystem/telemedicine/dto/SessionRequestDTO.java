package com.sliit.hospitalManagementSystem.telemedicine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionRequestDTO {

    private String appointmentId;
    private String doctorId;
    private String patientId;
}
