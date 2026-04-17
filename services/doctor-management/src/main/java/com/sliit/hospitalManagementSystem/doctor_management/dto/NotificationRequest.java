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

    // The category of notification being sent (e.g., BOOKED, CANCELLED)
    private NotificationType type;
    // Name of the individual receiving the notification
    private String recipientName;
    // Email address of the recipient
    private String recipientEmail;
    // Phone number of the recipient for SMS notifications
    private String recipientPhone;

    // Reference ID of the associated appointment
    private String appointmentId;
    // Name of the doctor involved in the event
    private String doctorName;
    // Name of the patient involved in the event
    private String patientName;
    // Date of the scheduled appointment
    private String appointmentDate;
    // Time of the scheduled appointment
    private String appointmentTime;
    // Medical specialty related to the appointment
    private String specialty;
    
    // Brief explanation for the medical visit
    private String reason;
    // Mode of meeting (Online or Physical)
    private String consultationType;
    // Final notes provided by the doctor after a session
    private String doctorNotes;

    public enum NotificationType {
        APPOINTMENT_BOOKED,
        APPOINTMENT_CANCELLED,
        APPOINTMENT_MODIFIED,
        CONSULTATION_COMPLETED
    }
}
