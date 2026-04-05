package com.sliit.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDocumentDto {
    private Long id;
    private Long patientId;
    private String documentName;
    private String documentUrl;
    private LocalDateTime uploadedAt;
}
