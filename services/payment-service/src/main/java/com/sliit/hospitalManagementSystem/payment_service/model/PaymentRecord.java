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
	private String customerEmail;
	private String customerName;
	private String customerPhone;
	private String items;
	// Appointment details for automatic booking
	private String patientId;
	private String doctorId;
	private String appointmentDate;
	private String consultationType;
	private String reason;
	private Instant updatedAt;
}

