package com.sliit.hospitalManagementSystem.notification_service.controller;

import com.sliit.hospitalManagementSystem.notification_service.dto.NotificationRequest;
import com.sliit.hospitalManagementSystem.notification_service.dto.NotificationResponse;
import com.sliit.hospitalManagementSystem.notification_service.dto.EmailRequest;
import com.sliit.hospitalManagementSystem.notification_service.service.EmailService;
import com.sliit.hospitalManagementSystem.notification_service.service.NotificationService;
import com.sliit.hospitalManagementSystem.notification_service.service.AppointmentNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;
    private final NotificationService notificationService;
    private final AppointmentNotificationService appointmentNotificationService;

    // Generic endpoint to send any type of notification (Email/SMS) based on request type
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendNotification(request));
    }

    // Direct endpoint to send a custom email message
    @PostMapping("/email")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailRequest request) {
        try {
            emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody(), request.isHtml());
            return ResponseEntity.ok(Map.of("message", "Email sent successfully to " + request.getTo()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // Sends a stylized email notification upon successful payment processing
    @PostMapping("/email/payment-success")
    public ResponseEntity<?> sendPaymentSuccessEmail(@RequestBody Map<String, String> request) {
        try {
            String toEmail = request.get("toEmail");
            String customerName = request.get("customerName");
            String orderId = request.get("orderId");
            String amount = request.get("amount");
            String paymentId = request.get("paymentId");
            String items = request.get("items");
            
            String subject = "Payment Successful - OminiHealth";
            String date = java.time.LocalDate.now().toString();
            String body = emailService.buildPaymentSuccessBody(customerName, orderId, amount, paymentId, items, date);
            
            emailService.sendEmail(toEmail, subject, body, true);
            return ResponseEntity.ok(Map.of("message", "Payment success email sent to " + toEmail));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // Sends an email notification if a payment attempt fails
    @PostMapping("/email/payment-failure")
    public ResponseEntity<?> sendPaymentFailureEmail(@RequestBody Map<String, String> request) {
        try {
            String toEmail = request.get("toEmail");
            String customerName = request.get("customerName");
            String orderId = request.get("orderId");
            String amount = request.get("amount");
            String errorMessage = request.get("errorMessage");
            
            String subject = "Payment Failed - OminiHealth";
            String date = java.time.LocalDate.now().toString();
            String body = emailService.buildPaymentFailureBody(customerName, orderId, amount, errorMessage, date);
            
            emailService.sendEmail(toEmail, subject, body, true);
            return ResponseEntity.ok(Map.of("message", "Payment failure email sent to " + toEmail));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // High-level endpoint to trigger a full appointment confirmation workflow
    @PostMapping("/appointment/confirm")
    public ResponseEntity<?> sendAppointmentConfirmation(@RequestBody Map<String, Object> request) {
        try {
            AppointmentNotificationService.AppointmentNotificationRequest notificationRequest = 
                new AppointmentNotificationService.AppointmentNotificationRequest(
                    request.get("appointmentId").toString(),
                    request.get("email").toString(),
                    request.get("customerName").toString(),
                    request.get("phoneNumber") != null ? request.get("phoneNumber").toString() : "",
                    request.get("doctorName").toString(),
                    java.time.LocalDateTime.parse(request.get("appointmentDate").toString()),
                    request.get("specialty") != null ? request.get("specialty").toString() : "General",
                    request.get("consultationType") != null ? request.get("consultationType").toString() : "In-Person",
                    request.get("reason") != null ? request.get("reason").toString() : "Regular checkup"
                );
            
            appointmentNotificationService.sendAppointmentConfirmation(notificationRequest);
            return ResponseEntity.ok(Map.of("message", "Appointment confirmation sent to " + notificationRequest.email()));
        } catch (Exception e) {
            log.error("Error sending appointment confirmation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // Sends a detailed HTML email confirming all details of a medical appointment
    @PostMapping("/email/appointment-confirmed")
    public ResponseEntity<?> sendAppointmentConfirmedEmail(@RequestBody Map<String, String> request) {
        try {
            String toEmail = request.get("toEmail");
            String customerName = request.get("customerName");
            String doctorName = request.get("doctorName");
            String appointmentDate = request.get("appointmentDate");
            String appointmentId = request.get("appointmentId");
            String specialty = request.getOrDefault("specialty", "General");
            String consultationType = request.getOrDefault("consultationType", "In-Person");
            String reason = request.getOrDefault("reason", "Regular checkup");
            
            String subject = "Appointment Confirmed - OminiHealth";
            String body = emailService.buildAppointmentBookedBody(
                customerName, 
                doctorName,
                customerName,
                appointmentDate,
                appointmentDate,
                appointmentId,
                specialty,
                consultationType,
                reason,
                "None provided"
            );
            
            emailService.sendEmail(toEmail, subject, body, true);
            return ResponseEntity.ok(Map.of("message", "Appointment confirmation email sent to " + toEmail));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // Sends a brief SMS message confirming an appointment
    @PostMapping("/sms/appointment-confirmed")
    public ResponseEntity<?> sendAppointmentConfirmedSMS(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String customerName = request.get("customerName");
            String message = request.get("message");
            
            // Logs the SMS intent as actual SMS provider integration is pending
            System.out.println("SMS to " + phoneNumber + ": " + message);
            
            return ResponseEntity.ok(Map.of("message", "Appointment confirmation SMS sent to " + phoneNumber));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // Basic health check endpoint to verify service availability
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service (Email-only) is UP");
    }
}
