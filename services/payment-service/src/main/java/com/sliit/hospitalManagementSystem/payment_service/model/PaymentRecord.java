package com.sliit.hospitalManagementSystem.payment_service.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentRecord {
	private String orderId;
	private String paymentId;
	private BigDecimal amount;
	private String currency;
	private PaymentState state;
	private String message;
	private Instant updatedAt;
}

