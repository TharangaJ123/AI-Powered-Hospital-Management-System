package com.sliit.hospitalManagementSystem.payment_service.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentStatusResponse {
	// The unique identifier for the order
	private String orderId;
	// The payment ID returned by PayHere
	private String paymentId;
	// Current status of the payment (e.g., SUCCESS, FAILED, PENDING)
	private String status;
	// Descriptive message regarding the payment status
	private String message;
	// Timestamp of the last status update
	private Instant updatedAt;
}

