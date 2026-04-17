package com.sliit.hospitalManagementSystem.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    // Status indicating whether the email was successfully dispatched
    private boolean emailSent;

    // Error message detailing why email delivery failed, if applicable
    private String emailError;

    // Status indicating whether the SMS message was successfully dispatched
    private boolean smsSent;

    // Error message detailing why SMS delivery failed, if applicable
    private String smsError;

    // General status or feedback message regarding the overall notification process
    private String message;
}


