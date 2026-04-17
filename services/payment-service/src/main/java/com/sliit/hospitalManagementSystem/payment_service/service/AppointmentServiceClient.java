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

    // WebClient for making asynchronous HTTP requests to other microservices
    private final WebClient webClient;

    // Base URL for the Appointment microservice, defaults to localhost:8082
    @Value("${appointment.service.url:http://localhost:8082}")
    private String appointmentServiceUrl;

    // Triggers the creation of an appointment once a payment is successfully verified
    public void createAppointmentAfterPayment(String patientId, String doctorId, 
                                            String appointmentDate, String fullName, 
                                            String email, String phoneNumber, 
                                            String reason, String consultationType) {
        try {
            // Construct the appointment request body
            AppointmentRequest request = new AppointmentRequest(
                patientId, doctorId, appointmentDate, fullName, 
                email, phoneNumber, reason, consultationType
            );

            // Make an asynchronous POST request to the appointment service
            webClient.post()
                .uri(appointmentServiceUrl + "/api/appointments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> {
                    log.info("Appointment automatically created after payment: {}", response);
                })
                .doOnError(error -> log.error("Failed to create appointment after payment: {}", error.getMessage()))
                .onErrorResume(error -> Mono.empty())
                .subscribe();
                
        } catch (Exception e) {
            log.error("Error creating appointment after payment: {}", e.getMessage(), e);
        }
    }

    // Sends confirmation notifications (email/SMS) after an appointment is booked
    private void sendAppointmentConfirmationNotification(String fullName, String email, String phone, String appointmentResponse) {
        try {
            // Dispatch email notification via the Notification service
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

            // Dispatch SMS notification via the Notification service
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

    // Record representing the data structure for appointment creation requests
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
