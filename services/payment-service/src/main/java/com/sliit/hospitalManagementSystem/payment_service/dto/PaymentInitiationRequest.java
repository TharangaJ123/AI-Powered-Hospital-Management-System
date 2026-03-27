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

	@NotBlank
	private String orderId;

	@NotBlank
	private String items;

	@NotNull
	@DecimalMin(value = "0.01")
	private BigDecimal amount;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String phone;

	@NotBlank
	private String address;

	@NotBlank
	private String city;

	@NotBlank
	private String country;
}

