package com.sliit.hospitalManagementSystem.payment_service.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentStatusResponse {
	private String orderId;
	private String paymentId;
	private String status;
	private String message;
	private Instant updatedAt;
}

