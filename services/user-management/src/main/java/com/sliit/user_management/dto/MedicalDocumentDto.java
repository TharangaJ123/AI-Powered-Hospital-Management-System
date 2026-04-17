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
    // Unique identifier for the medical document
    private Long id;
    // ID of the patient to whom the document belongs
    private Long patientId;
    // The display name or title of the document
    private String documentName;
    // The storage URL or link to the actual document file
    private String documentUrl;
    // The timestamp when the document was uploaded
    private LocalDateTime uploadedAt;
}
