package com.sliit.hospitalManagementSystem.telemedicine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "telemedicine_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelemedicineSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String appointmentId;

    @Column(nullable = false)
    private String doctorId;

    @Column(nullable = false)
    private String patientId;

    @Column(nullable = false, unique = true)
    private String roomName;

    @Column(nullable = false)
    private String sessionUrl;

    @Column(nullable = false)
    private String status; // ACTIVE, ENDED

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
