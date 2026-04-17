package com.sliit.contact_service.dto;

import lombok.Data;

@Data
public class ContactRequest {
    // Name of the individual submitting the contact form
    private String name;
    // Email address for replying to the inquiry
    private String email;
    // Subject line of the contact message
    private String subject;
    // The main body text of the inquiry
    private String message;
    // Optional ID linking the request to a registered patient
    private Long patientId;
}
