package com.sliit.user_management.repository;

import com.sliit.user_management.model.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    List<FinancialTransaction> findByUserId(Long userId);
    
    @Query("SELECT SUM(f.amount) FROM FinancialTransaction f WHERE f.status = 'SUCCESS'")
    Double getTotalRevenue();
    
    long countByStatus(String status);
    
    List<FinancialTransaction> findAllByOrderByTransactionDateDesc();
}
