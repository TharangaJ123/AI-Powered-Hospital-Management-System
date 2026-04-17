package com.sliit.contact_service.dto;

import lombok.Data;

@Data
public class ContactRequest {
    private String name;
    private String email;
    private String subject;
    private String message;
    private Long patientId;
}
