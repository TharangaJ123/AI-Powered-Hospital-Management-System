package com.sliit.hospitalManagementSystem.notification_service.controller;

import com.sliit.hospitalManagementSystem.notification_service.dto.NotificationRequest;
import com.sliit.hospitalManagementSystem.notification_service.dto.NotificationResponse;
import com.sliit.hospitalManagementSystem.notification_service.dto.EmailRequest;
import com.sliit.hospitalManagementSystem.notification_service.service.EmailService;
import com.sliit.hospitalManagementSystem.notification_service.service.NotificationService;
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

    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendNotification(request));
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailRequest request) {
        try {
            emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody(), request.isHtml());
            return ResponseEntity.ok(Map.of("message", "Email sent successfully to " + request.getTo()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service (Email-only) is UP");
    }
}
