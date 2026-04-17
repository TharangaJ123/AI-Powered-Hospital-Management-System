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

    // Specifies the category of notification (e.g., BOOKED, CANCELLED)
    @NotNull(message = "Notification type is required")
    private NotificationType type;

    // Full name of the user receiving the notification
    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    // Target email address for the notification
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String recipientEmail;

    // Target phone number for SMS notifications (optional)
    private String recipientPhone;

    // Unique identifier for the related medical appointment
    private String appointmentId;

    // Name of the doctor assigned to the appointment
    private String doctorName;

    // Name of the patient involved in the appointment
    private String patientName;

    // The date scheduled for the appointment
    private String appointmentDate;   

    // The specific time scheduled for the appointment
    private String appointmentTime;   

    // Medical area of expertise of the doctor
    private String specialty;
    
    // The patient's reason for seeking a consultation
    private String reason;

    // Mode of meeting (e.g., In-Person, Telemedicine)
    private String consultationType;

    // Clinical notes or instructions provided by the doctor
    private String doctorNotes;

    // Defined types of system-wide notifications
    public enum NotificationType {
        // Triggered when a new appointment is successfully scheduled
        APPOINTMENT_BOOKED,
        // Triggered when an existing appointment is removed
        APPOINTMENT_CANCELLED,
        // Triggered when appointment details (time/doctor) are changed
        APPOINTMENT_MODIFIED,
        // Triggered after a medical consultation concludes
        CONSULTATION_COMPLETED,
        // Triggered when a new user joins the platform
        USER_REGISTERED
    }
}
