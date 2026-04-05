package com.sliit.user_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // can be patient or doctor
    
    private String transactionType; // e.g. APPOINTMENT_FEE, SUBSCRIPTION
    
    private Double amount;
    
    private String status; // SUCCESS, PENDING, FAILED

    @Builder.Default
    private LocalDateTime transactionDate = LocalDateTime.now();
}
