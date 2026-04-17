package com.sliit.user_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinancialTransactionDto {
    // Unique identifier for the transaction
    private Long id;
    // ID of the user associated with the transaction
    private Long userId;
    // The type of transaction (e.g., PAYMENT, REFUND)
    private String transactionType;
    // The monetary value of the transaction
    private Double amount;
    // Current status of the transaction (e.g., SUCCESS, PENDING, FAILED)
    private String status;
    // The date and time when the transaction occurred
    private LocalDateTime transactionDate;
}
