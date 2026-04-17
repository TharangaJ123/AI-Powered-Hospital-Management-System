package com.sliit.hospitalManagementSystem.payment_service.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentInitiationResponse {
	// The URL where the user should be redirected to complete the payment
	private String checkoutUrl;
	// A map of key-value pairs required for the PayHere payment form submission
	private Map<String, String> paymentFormFields;
}

