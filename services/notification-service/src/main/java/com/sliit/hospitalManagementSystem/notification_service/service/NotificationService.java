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
    private final SmsService smsService;

    /**
     * Dispatch an HTML template email and SMS (if phone provided) based on the NotificationType.
     */
    public NotificationResponse sendNotification(NotificationRequest req) {
        log.info("Processing {} notification for {}({})", req.getType(), req.getRecipientEmail(), req.getRecipientPhone());

        boolean emailSent = false;
        String emailErr = null;
        boolean smsSent = false;
        String smsErr = null;

        String emailSubject;
        String emailBody;
        String smsBody = null;

        switch (req.getType()) {

            case APPOINTMENT_BOOKED -> {
                emailSubject = "✅ Appointment Confirmed \u2013 MediConnect";
                emailBody = emailService.buildAppointmentBookedBody(
                        req.getRecipientName(), req.getDoctorName(), req.getPatientName(),
                        req.getAppointmentDate(), req.getAppointmentTime(),
                        req.getAppointmentId(), req.getSpecialty());
                smsBody = smsService.buildAppointmentBookedSms(
                        req.getPatientName(), req.getDoctorName(), req.getAppointmentDate(),
                        req.getAppointmentTime(), req.getAppointmentId());
            }

            case APPOINTMENT_CANCELLED -> {
                emailSubject = "❌ Appointment Cancelled \u2013 MediConnect";
                emailBody = emailService.buildAppointmentCancelledBody(
                        req.getRecipientName(), req.getDoctorName(),
                        req.getAppointmentDate(), req.getAppointmentTime(), req.getAppointmentId());
                smsBody = smsService.buildAppointmentCancelledSms(
                        req.getPatientName(), req.getDoctorName(), req.getAppointmentDate(),
                        req.getAppointmentTime());
            }

            case APPOINTMENT_MODIFIED -> {
                emailSubject = "✏️ Appointment Updated \u2013 MediConnect";
                emailBody = emailService.buildAppointmentBookedBody(   // reuse booked template
                        req.getRecipientName(), req.getDoctorName(), req.getPatientName(),
                        req.getAppointmentDate(), req.getAppointmentTime(),
                        req.getAppointmentId(), req.getSpecialty());
                smsBody = smsService.buildAppointmentModifiedSms(
                        req.getPatientName(), req.getDoctorName(), req.getAppointmentDate(),
                        req.getAppointmentTime());
            }

            case CONSULTATION_COMPLETED -> {
                emailSubject = "🎓 Consultation Completed \u2013 MediConnect";
                emailBody = emailService.buildConsultationCompletedBody(
                        req.getRecipientName(), req.getDoctorName(),
                        req.getAppointmentDate(), req.getAppointmentId());
                smsBody = smsService.buildConsultationCompletedSms(
                        req.getPatientName(), req.getDoctorName());
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

        // Send SMS if phone number is provided
        if (req.getRecipientPhone() != null && !req.getRecipientPhone().isEmpty() && smsBody != null) {
            try {
                smsService.sendSms(req.getRecipientPhone(), smsBody);
                smsSent = true;
            } catch (Exception e) {
                smsErr = e.getMessage();
                log.error("SMS delivery failed: {}", e.getMessage());
            }
        }

        String summaryMessage = "Notification processed. Email: " + (emailSent ? "Sent" : "Failed") + ", SMS: " + (req.getRecipientPhone() != null ? (smsSent ? "Sent" : "Failed") : "Not Requested");

        return NotificationResponse.builder()
                .emailSent(emailSent)
                .emailError(emailErr)
                .smsSent(smsSent)
                .smsError(smsErr)
                .message(summaryMessage)
                .build();
    }
}
