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

    // Dispatch an HTML template email and SMS (if phone provided) based on the NotificationType.
    public NotificationResponse sendNotification(NotificationRequest req) {
        log.info("Processing {} notification for {}({})", req.getType(), req.getRecipientEmail(), req.getRecipientPhone());

        // Tracking variables for delivery status and error capture
        boolean emailSent = false;
        String emailErr = null;
        boolean smsSent = false;
        String smsErr = null;

        String emailSubject;
        String emailBody;
        String smsBody = null;

        // Selection logic to determine the appropriate HTML and text templates based on business event
        switch (req.getType()) {

            case APPOINTMENT_BOOKED -> {
                emailSubject = "Appointment Confirmed \u2013 OmniHealth";
                emailBody = emailService.buildAppointmentBookedBody(
                        req.getRecipientName(), req.getDoctorName(), req.getPatientName(),
                        req.getAppointmentDate(), req.getAppointmentTime(),
                        req.getAppointmentId(), req.getSpecialty(),
                        req.getConsultationType(), req.getReason(), req.getDoctorNotes());
                smsBody = smsService.buildAppointmentBookedSms(
                        req.getPatientName(), req.getDoctorName(), req.getAppointmentDate(),
                        req.getAppointmentTime(), req.getAppointmentId());
            }

            case APPOINTMENT_CANCELLED -> {
                emailSubject = "Appointment Cancelled \u2013 OmniHealth";
                emailBody = emailService.buildAppointmentCancelledBody(
                        req.getRecipientName(), req.getDoctorName(),
                        req.getAppointmentDate(), req.getAppointmentTime(), req.getAppointmentId());
               smsBody = smsService.buildAppointmentCancelledSms(
                       req.getPatientName(), req.getDoctorName(), req.getAppointmentDate(),
                       req.getAppointmentTime());
            }

            case APPOINTMENT_MODIFIED -> {
                emailSubject = "Appointment Updated \u2013 OmniHealth";
                emailBody = emailService.buildAppointmentBookedBody(   // reuse booked template
                        req.getRecipientName(), req.getDoctorName(), req.getPatientName(),
                        req.getAppointmentDate(), req.getAppointmentTime(),
                        req.getAppointmentId(), req.getSpecialty(),
                        req.getConsultationType(), req.getReason(), req.getDoctorNotes());
               smsBody = smsService.buildAppointmentModifiedSms(
                       req.getPatientName(), req.getDoctorName(), req.getAppointmentDate(),
                       req.getAppointmentTime());
            }

            case CONSULTATION_COMPLETED -> {
                emailSubject = "Consultation Completed \u2013 OmniHealth";
                emailBody = emailService.buildConsultationCompletedBody(
                        req.getRecipientName(), req.getDoctorName(),
                        req.getAppointmentDate(), req.getAppointmentId());
               smsBody = smsService.buildConsultationCompletedSms(
                       req.getPatientName(), req.getDoctorName());
            }
            
            case USER_REGISTERED -> {
                emailSubject = "Welcome to OmniHealth - Registration Successful!";
                emailBody = emailService.buildUserRegisteredBody(req.getRecipientName(), req.getRecipientEmail());
               smsBody = smsService.buildUserRegisteredSms(req.getRecipientName());
            }

            default -> throw new IllegalArgumentException("Unknown type: " + req.getType());
        }

        // Attempt to deliver the generated HTML email
        try {
            emailService.sendEmail(req.getRecipientEmail(), emailSubject, emailBody, true);
            emailSent = true;
        } catch (Exception e) {
            emailErr = e.getMessage();
            log.error("Email delivery failed: {}", e.getMessage());
        }

        // Attempt to deliver the SMS if a phone number was provided in the request
        if (req.getRecipientPhone() != null && !req.getRecipientPhone().isEmpty() && smsBody != null) {
            try {
                smsService.sendSms(req.getRecipientPhone(), smsBody);
                smsSent = true;
            } catch (Exception e) {
                smsErr = e.getMessage();
                log.error("SMS delivery failed: {}", e.getMessage());
            }
        }
        
        // Construct a summary report for the calling microservice
        String summaryMessage = "Notification processed. Email: " + (emailSent ? "Sent" : "Failed") + ", SMS: " + (smsSent ? "Sent" : (smsBody == null ? "Skipped" : "Failed"));

        return NotificationResponse.builder()
                .emailSent(emailSent)
                .emailError(emailErr)
                .smsSent(smsSent)
                .smsError(smsErr)
                .message(summaryMessage)
                .build();
    }
}
