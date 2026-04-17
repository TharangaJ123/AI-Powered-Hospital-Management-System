package com.sliit.hospitalManagementSystem.notification_service.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
public class SmsService {

    @Value("${notify.user.id}")
    private String userId;

    @Value("${notify.api.key}")
    private String apiKey;

    @Value("${notify.sender.id}")
    private String senderId;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String NOTIFY_API_URL = "https://app.notify.lk/api/v1/send";

    @PostConstruct
    public void init() {
        log.info("Notify.lk SMS service initialized. SenderId: {}", senderId);
    }

    public void sendSms(String to, String body) {
        log.info("Sending SMS to {} via Notify.lk...", to);
        try {
            // Notify.lk expects 947XXXXXXXX format (11 digits)
            String formattedPhone = to.replaceAll("\\D", "");
            if (formattedPhone.startsWith("0")) {
                formattedPhone = "94" + formattedPhone.substring(1);
            } else if (!formattedPhone.startsWith("94") && formattedPhone.length() == 9) {
                formattedPhone = "94" + formattedPhone;
            }

            // Build form data as POST body (application/x-www-form-urlencoded)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("user_id", userId);
            formData.add("api_key", apiKey);
            formData.add("sender_id", senderId);
            formData.add("to", formattedPhone);
            formData.add("message", body);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(NOTIFY_API_URL, request, String.class);
            log.info("Notify.lk API Response [{}]: {}", response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage());
            throw new RuntimeException("SMS delivery failed via Notify.lk: " + e.getMessage());
        }
    }

    // --- Template Builders ---

    public String buildUserRegisteredSms(String userName) {
        return String.format("Hi %s! Welcome to OminiHealth. Your registration was successful. Log in anytime for premium healthcare services.", userName);
    }

    public String buildAppointmentBookedSms(String patientName, String doctorName, String date, String time, String appointmentId) {
        return String.format("Hi %s, your appointment with Dr. %s is confirmed on %s at %s. ID: %s. - OmniHealth",
                patientName, doctorName, date, time, appointmentId);
    }

    public String buildAppointmentCancelledSms(String patientName, String doctorName, String date, String time) {
        return String.format("Hi %s, your appointment with Dr. %s on %s at %s has been cancelled. Contact us for details. - OmniHealth",
                patientName, doctorName, date, time);
    }

    public String buildAppointmentModifiedSms(String patientName, String doctorName, String date, String time) {
        return String.format("Hi %s, your appointment with Dr. %s has been updated. New time: %s at %s. - OmniHealth",
                patientName, doctorName, date, time);
    }

    public String buildConsultationCompletedSms(String patientName, String doctorName) {
        return String.format("Hi %s, your consultation with Dr. %s is complete. Wishing you good health! - OmniHealth",
                patientName, doctorName);
    }
}
