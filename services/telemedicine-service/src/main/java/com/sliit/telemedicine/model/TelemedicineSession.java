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

    // Primary key for the telemedicine visit record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The unique ID of the corresponding appointment
    @Column(name = "appointment_id", nullable = false, unique = true)
    private Long appointmentId;

    // The assigned Jitsi room name for this session
    @Column(name = "room_name", nullable = false)
    private String roomName;

    // Current status of the session (e.g., CREATED, COMPLETED)
    @Enumerated(EnumType.STRING)
    @Column(name = "visit_status", length = 50, nullable = false)
    private SessionStatus status;

    // The timestamp when the session record was created
    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Defines the possible states of a telemedicine session
    public enum SessionStatus {
        CREATED, ONGOING, COMPLETED
    }
}
