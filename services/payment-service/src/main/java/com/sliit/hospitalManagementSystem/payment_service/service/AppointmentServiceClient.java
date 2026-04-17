package com.sliit.hospitalManagementSystem.payment_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceClient {

    private final WebClient webClient;

    @Value("${appointment.service.url:http://localhost:8082}")
    private String appointmentServiceUrl;

    public void createAppointmentAfterPayment(String patientId, String doctorId, 
                                            String appointmentDate, String fullName, 
                                            String email, String phoneNumber, 
                                            String reason, String consultationType) {
        try {
            AppointmentRequest request = new AppointmentRequest(
                patientId, doctorId, appointmentDate, fullName, 
                email, phoneNumber, reason, consultationType
            );

            webClient.post()
                .uri(appointmentServiceUrl + "/api/appointments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> {
                    log.info("Appointment automatically created after payment: {}", response);
                    // Do not send email here, appointment-service already handles it!
                })
                .doOnError(error -> log.error("Failed to create appointment after payment: {}", error.getMessage()))
                .onErrorResume(error -> Mono.empty())
                .subscribe();
                
        } catch (Exception e) {
            log.error("Error creating appointment after payment: {}", e.getMessage(), e);
        }
    }

    private void sendAppointmentConfirmationNotification(String fullName, String email, String phone, String appointmentResponse) {
        try {
            // Send email notification
            webClient.post()
                .uri("http://localhost:8085/api/notifications/email/appointment-confirmed")
                .bodyValue(java.util.Map.of(
                    "toEmail", email,
                    "customerName", fullName,
                    "appointmentDetails", appointmentResponse
                ))
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Appointment confirmation email sent to {}: {}", email, response))
                .doOnError(error -> log.error("Failed to send appointment confirmation email: {}", error.getMessage()))
                .onErrorResume(error -> Mono.empty())
                .subscribe();

            // Send SMS notification
            webClient.post()
                .uri("http://localhost:8085/api/notifications/sms/appointment-confirmed")
                .bodyValue(java.util.Map.of(
                    "phoneNumber", phone,
                    "customerName", fullName,
                    "message", "Your appointment has been confirmed! " + appointmentResponse
                ))
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Appointment confirmation SMS sent to {}: {}", phone, response))
                .doOnError(error -> log.error("Failed to send appointment confirmation SMS: {}", error.getMessage()))
                .onErrorResume(error -> Mono.empty())
                .subscribe();

        } catch (Exception e) {
            log.error("Error sending appointment confirmation notifications: {}", e.getMessage(), e);
        }
    }

    public record AppointmentRequest(
        String patientId,
        String doctorId,
        String appointmentDate,
        String fullName,
        String email,
        String phoneNumber,
        String reason,
        String consultationType
    ) {}
}
