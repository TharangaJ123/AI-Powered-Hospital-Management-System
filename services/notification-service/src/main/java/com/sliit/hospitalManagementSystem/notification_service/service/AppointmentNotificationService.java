package com.sliit.hospitalManagementSystem.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentNotificationService {

    private final EmailService emailService;

    // Orchestrates the multi-channel (email/log) confirmation for a medical appointment
    public void sendAppointmentConfirmation(AppointmentNotificationRequest request) {
        try {
            log.info("Sending appointment confirmation for appointment ID: {}", request.getAppointmentId());
            
            // Constructs the specialized HTML subject and body for the confirmation email
            String emailSubject = "Appointment Confirmed - OminiHealth";
            String emailBody = emailService.buildAppointmentBookedBody(
                request.getCustomerName(),
                request.getDoctorName(),
                request.getCustomerName(),
                request.getAppointmentDate().toLocalDate().toString(),
                request.getAppointmentDate().toLocalTime().toString(),
                request.getAppointmentId().toString(),
                request.getSpecialty() != null ? request.getSpecialty() : "General",
                request.getConsultationType() != null ? request.getConsultationType() : "In-Person",
                request.getReason() != null ? request.getReason() : "Regular checkup",
                "None provided"
            );
            
            // Dispatches the email through the central EmailService
            emailService.sendEmail(request.getEmail(), emailSubject, emailBody, true);
            log.info("Appointment confirmation email sent to: {}", request.getEmail());
            
            // Placeholder for future SMS provider integration
            log.info("Appointment confirmation sent successfully");
            
        } catch (Exception e) {
            log.error("Error sending appointment confirmation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send appointment confirmation", e);
        }
    }

    // Data record containing all necessary details for an appointment-specific notification
    public record AppointmentNotificationRequest(
        // ID of the appointment in the system
        String appointmentId,
        // Recipient's email address
        String email,
        // Recipient's full name
        String customerName,
        // Recipient's phone number for SMS
        String phoneNumber,
        // Assigned doctor's name
        String doctorName,
        // Scheduled date and time of the appointment
        LocalDateTime appointmentDate,
        // Medical specialty of the doctor
        String specialty,
        // Type of meeting (In-Person/Video)
        String consultationType,
        // Patient's stated reason for the visit
        String reason
    ) {
        public String getAppointmentId() { return appointmentId; }
        public String getEmail() { return email; }
        public String getCustomerName() { return customerName; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getDoctorName() { return doctorName; }
        public LocalDateTime getAppointmentDate() { return appointmentDate; }
        public String getSpecialty() { return specialty; }
        public String getConsultationType() { return consultationType; }
        public String getReason() { return reason; }
    }
}
