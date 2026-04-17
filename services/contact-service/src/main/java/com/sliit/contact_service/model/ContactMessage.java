package com.sliit.contact_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactMessage {
    // Unique identifier for the contact message entry
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Full name of the person sending the inquiry
    private String name;

    // Email address of the sender for future correspondence
    private String email;

    // The topic or purpose of the contact message
    private String subject;

    // The detailed content of the message sent by the user
    @Column(columnDefinition = "TEXT")
    private String message;

    // Reference ID to the patient, if the sender is a registered user
    private Long patientId;
    
    // Current processing state of the message (e.g., PENDING, REPLIED)
    private String status;
    
    // The textual response provided by the hospital administrator
    @Column(columnDefinition = "TEXT")
    private String adminReply;
    
    // Timestamp recording when the administrator provided a reply
    private LocalDateTime repliedAt;

    // Automatic timestamp recording when the message was first received
    @CreationTimestamp
    private LocalDateTime createdAt;
}
