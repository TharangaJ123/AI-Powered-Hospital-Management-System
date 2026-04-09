package com.sliit.telemedicine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemedicine_visits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemedicineSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false, unique = true)
    private Long appointmentId;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_status", length = 50, nullable = false)
    private SessionStatus status;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum SessionStatus {
        CREATED, ONGOING, COMPLETED
    }
}
