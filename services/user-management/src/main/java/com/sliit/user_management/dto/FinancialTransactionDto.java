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
    private Long id;
    private Long userId;
    private String transactionType;
    private Double amount;
    private String status;
    private LocalDateTime transactionDate;
}
