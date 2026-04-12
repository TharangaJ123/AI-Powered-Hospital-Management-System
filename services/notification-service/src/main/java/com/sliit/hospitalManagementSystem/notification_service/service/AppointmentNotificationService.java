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

    public void sendAppointmentConfirmation(AppointmentNotificationRequest request) {
        try {
            log.info("Sending appointment confirmation for appointment ID: {}", request.getAppointmentId());
            
            // Send email notification
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
            
            emailService.sendEmail(request.getEmail(), emailSubject, emailBody, true);
            log.info("Appointment confirmation email sent to: {}", request.getEmail());
            
            // Note: SMS functionality can be added here later
            log.info("Appointment confirmation sent successfully");
            
        } catch (Exception e) {
            log.error("Error sending appointment confirmation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send appointment confirmation", e);
        }
    }

    public record AppointmentNotificationRequest(
        String appointmentId,
        String email,
        String customerName,
        String phoneNumber,
        String doctorName,
        LocalDateTime appointmentDate,
        String specialty,
        String consultationType,
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
