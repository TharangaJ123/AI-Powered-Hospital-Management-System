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
public class NotificationServiceClient {

    private final WebClient webClient;

    @Value("${notification.service.url:http://localhost:8085}")
    private String notificationServiceUrl;

    public void sendPaymentSuccessEmail(String toEmail, String customerName, String orderId, 
                                       String amount, String paymentId, String items) {
        try {
            PaymentEmailRequest request = new PaymentEmailRequest(
                toEmail, customerName, orderId, amount, paymentId, items
            );

            webClient.post()
                .uri(notificationServiceUrl + "/api/notifications/email/payment-success")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Payment success email sent to {}: {}", toEmail, response))
                .doOnError(error -> log.error("Failed to send payment success email to {}: {}", toEmail, error.getMessage()))
                .onErrorResume(error -> Mono.empty())
                .subscribe();
                
        } catch (Exception e) {
            log.error("Error sending payment success email: {}", e.getMessage(), e);
        }
    }

    public void sendPaymentFailureEmail(String toEmail, String customerName, String orderId, 
                                       String amount, String errorMessage) {
        try {
            PaymentFailureEmailRequest request = new PaymentFailureEmailRequest(
                toEmail, customerName, orderId, amount, errorMessage
            );

            webClient.post()
                .uri(notificationServiceUrl + "/api/notifications/email/payment-failure")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Payment failure email sent to {}: {}", toEmail, response))
                .doOnError(error -> log.error("Failed to send payment failure email to {}: {}", toEmail, error.getMessage()))
                .onErrorResume(error -> Mono.empty())
                .subscribe();
                
        } catch (Exception e) {
            log.error("Error sending payment failure email: {}", e.getMessage(), e);
        }
    }

    public record PaymentEmailRequest(
        String toEmail,
        String customerName,
        String orderId,
        String amount,
        String paymentId,
        String items
    ) {}

    public record PaymentFailureEmailRequest(
        String toEmail,
        String customerName,
        String orderId,
        String amount,
        String errorMessage
    ) {}
}
