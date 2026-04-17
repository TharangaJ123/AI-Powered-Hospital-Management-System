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

    private boolean emailSent;
    private String emailError;
    private boolean smsSent;
    private String smsError;
    private String message;
}


