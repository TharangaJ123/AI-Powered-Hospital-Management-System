package com.sliit.telemedicine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemedicine_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemedicineSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long appointmentId;

    @Column(nullable = false)
    private String roomName;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum SessionStatus {
        CREATED, ONGOING, COMPLETED
    }
}
