package com.sliit.hospitalManagementSystem.payment_service.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentInitiationResponse {
	private String checkoutUrl;
	private Map<String, String> paymentFormFields;
}

