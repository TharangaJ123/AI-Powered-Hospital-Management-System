package com.sliit.hospitalManagementSystem.doctor_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private NotificationType type;
    private String recipientName;
    private String recipientEmail;
    private String recipientPhone;

    private String appointmentId;
    private String doctorName;
    private String patientName;
    private String appointmentDate;
    private String appointmentTime;
    private String specialty;
    
    // Summary fields
    private String reason;
    private String consultationType;
    private String doctorNotes;

    public enum NotificationType {
        APPOINTMENT_BOOKED,
        APPOINTMENT_CANCELLED,
        APPOINTMENT_MODIFIED,
        CONSULTATION_COMPLETED
    }
}
