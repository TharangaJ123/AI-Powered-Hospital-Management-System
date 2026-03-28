package com.sliit.user_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long doctorId;
    private Long patientId;
    private int rating; // 1-5
    
    @Column(length = 1000)
    private String comment;

    private LocalDateTime createdAt;
}
