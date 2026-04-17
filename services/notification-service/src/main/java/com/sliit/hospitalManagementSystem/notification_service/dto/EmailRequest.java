package com.sliit.hospitalManagementSystem.notification_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    // The destination email address for the message
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String to;

    // The subject line of the email
    @NotBlank(message = "Subject is required")
    private String subject;

    // The main content of the email
    @NotBlank(message = "Message body is required")
    private String body;

    // Flag indicating if the body should be processed as HTML (true) or plain text (false)
    private boolean html;
}
