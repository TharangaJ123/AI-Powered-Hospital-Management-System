package com.sliit.hospitalManagementSystem.payment_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInitiationRequest {

	// The unique identifier for the order being paid for
	@NotBlank
	private String orderId;

	// Description or list of items being purchased
	@NotBlank
	private String items;

	// Total payment amount, must be at least 0.01
	@NotNull
	@DecimalMin(value = "0.01")
	private BigDecimal amount;

	// Customer's first name
	@NotBlank
	private String firstName;

	// Customer's last name
	@NotBlank
	private String lastName;

	// Customer's valid email address for notifications
	@NotBlank
	@Email
	private String email;

	// Customer's contact phone number
	@NotBlank
	private String phone;

	// Customer's physical street address
	@NotBlank
	private String address;

	// Customer's city of residence
	@NotBlank
	private String city;

	// Customer's country
	@NotBlank
	private String country;

	// Unique ID of the patient (used for appointment processing)
	private String patientId;
	// Unique ID of the doctor (used for appointment processing)
	private String doctorId;
	// Requested date for the appointment
	private String appointmentDate;
	// The type of consultation (e.g., Online, Physical)
	private String consultationType;
	// Brief description of the medical reason for the visit
	private String reason;
}

