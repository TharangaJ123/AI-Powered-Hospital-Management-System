package com.sliit.hospitalManagementSystem.notification_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unified request used by other microservices (e.g. Appointment Service)
 * to trigger both email AND SMS in one call.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String recipientEmail;

    private String recipientPhone;

    private String appointmentId;
    private String doctorName;
    private String patientName;
    private String appointmentDate;   
    private String appointmentTime;   
    private String specialty;
    
    // New fields for full summary
    private String reason;
    private String consultationType;
    private String doctorNotes;

    public enum NotificationType {
        APPOINTMENT_BOOKED,
        APPOINTMENT_CANCELLED,
        APPOINTMENT_MODIFIED,
        CONSULTATION_COMPLETED,
        USER_REGISTERED
    }
}
