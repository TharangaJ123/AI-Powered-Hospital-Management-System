package com.sliit.hospitalManagementSystem.notification_service.service;

import com.sliit.hospitalManagementSystem.notification_service.dto.NotificationRequest;
import com.sliit.hospitalManagementSystem.notification_service.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;

    /**
     * Dispatch an HTML template email based on the NotificationType.
     * SMS is completely removed per user request.
     */
    public NotificationResponse sendNotification(NotificationRequest req) {
        log.info("Processing {} email notification for {}", req.getType(), req.getRecipientEmail());

        boolean emailSent = false;
        String emailErr = null;

        String emailSubject;
        String emailBody;

        switch (req.getType()) {

            case APPOINTMENT_BOOKED -> {
                emailSubject = "✅ Appointment Confirmed \u2013 MediConnect";
                emailBody = emailService.buildAppointmentBookedBody(
                        req.getRecipientName(), req.getDoctorName(), req.getPatientName(),
                        req.getAppointmentDate(), req.getAppointmentTime(),
                        req.getAppointmentId(), req.getSpecialty());
            }

            case APPOINTMENT_CANCELLED -> {
                emailSubject = "❌ Appointment Cancelled \u2013 MediConnect";
                emailBody = emailService.buildAppointmentCancelledBody(
                        req.getRecipientName(), req.getDoctorName(),
                        req.getAppointmentDate(), req.getAppointmentTime(), req.getAppointmentId());
            }

            case APPOINTMENT_MODIFIED -> {
                emailSubject = "✏️ Appointment Updated \u2013 MediConnect";
                emailBody = emailService.buildAppointmentBookedBody(   // reuse booked template
                        req.getRecipientName(), req.getDoctorName(), req.getPatientName(),
                        req.getAppointmentDate(), req.getAppointmentTime(),
                        req.getAppointmentId(), req.getSpecialty());
            }

            case CONSULTATION_COMPLETED -> {
                emailSubject = "🎓 Consultation Completed \u2013 MediConnect";
                emailBody = emailService.buildConsultationCompletedBody(
                        req.getRecipientName(), req.getDoctorName(),
                        req.getAppointmentDate(), req.getAppointmentId());
            }

            default -> throw new IllegalArgumentException("Unknown type: " + req.getType());
        }

        // Send template email
        try {
            emailService.sendEmail(req.getRecipientEmail(), emailSubject, emailBody, true);
            emailSent = true;
        } catch (Exception e) {
            emailErr = e.getMessage();
            log.error("Email delivery failed: {}", e.getMessage());
        }

        return NotificationResponse.builder()
                .emailSent(emailSent)
                .emailError(emailErr)
                .message(emailSent ? "Notification sent via email" : "Notification email failed")
                .build();
    }
}
