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
	// The unique identifier for the order
	private String orderId;
	// The payment ID provided by the payment gateway
	private String paymentId;
	// The transaction amount
	private BigDecimal amount;
	// The currency used (e.g., LKR)
	private String currency;
	// The current state of the payment (e.g., INITIATED, SUCCESS)
	private PaymentState state;
	// Human-readable status message
	private String message;
	// Customer's email for contact and receipts
	private String customerEmail;
	// Customer's full name
	private String customerName;
	// Customer's contact number
	private String customerPhone;
	// Description of items/services purchased
	private String items;
	// ID of the patient (for automatic appointment creation)
	private String patientId;
	// ID of the doctor (for automatic appointment creation)
	private String doctorId;
	// Requested date for the appointment
	private String appointmentDate;
	// Type of consultation (e.g., Physical, Online)
	private String consultationType;
	// Patient's reason for the consultation
	private String reason;
	// Timestamp of the last update to this record
	private Instant updatedAt;
}

