package com.sliit.hospitalManagementSystem.payment_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayHereNotifyRequest {
	private String merchantId;
	private String orderId;
	private String paymentId;
	private String payhereAmount;
	private String payhereCurrency;
	private String statusCode;
	private String md5sig;
	private String method;
	private String statusMessage;
}

