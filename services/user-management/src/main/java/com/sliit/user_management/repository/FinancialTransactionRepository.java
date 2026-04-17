package com.sliit.user_management.repository;

import com.sliit.user_management.model.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    // Custom query to find all financial transactions for a specific user
    List<FinancialTransaction> findByUserId(Long userId);
    
    // JPQL query to calculate the total revenue from all successful transactions
    @Query("SELECT SUM(f.amount) FROM FinancialTransaction f WHERE f.status = 'SUCCESS'")
    Double getTotalRevenue();
    
    // Method to count transactions based on their current status (e.g., SUCCESS, PENDING)
    long countByStatus(String status);
    
    // Retrieves all transactions sorted by date in descending order (newest first)
    List<FinancialTransaction> findAllByOrderByTransactionDateDesc();
}
