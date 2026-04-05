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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Long patientId;
    
    private String status; // PENDING, REPLIED
    
    @Column(columnDefinition = "TEXT")
    private String adminReply;
    
    private LocalDateTime repliedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
