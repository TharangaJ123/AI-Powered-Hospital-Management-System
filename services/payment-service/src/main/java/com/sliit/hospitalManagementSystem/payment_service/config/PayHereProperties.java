package com.sliit.hospitalManagementSystem.payment_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "payhere")
public class PayHereProperties {

	private boolean sandbox = true;

	@NotBlank
	private String merchantId;

	@NotBlank
	private String merchantSecret;

	@NotBlank
	private String currency = "LKR";

	@NotBlank
	private String returnUrl;

	@NotBlank
	private String cancelUrl;

	@NotBlank
	private String notifyUrl;

	public String getCheckoutUrl() {
		return sandbox
				? "https://sandbox.payhere.lk/pay/checkout"
				: "https://www.payhere.lk/pay/checkout";
	}
}

