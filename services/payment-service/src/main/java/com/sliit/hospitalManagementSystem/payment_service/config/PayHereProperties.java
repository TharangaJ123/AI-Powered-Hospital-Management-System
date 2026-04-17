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

	// Flag to toggle between PayHere sandbox and production environments
	private boolean sandbox = true;

	// The merchant identifier provided by PayHere
	@NotBlank
	private String merchantId;

	// The secret key used for security hash generation
	@NotBlank
	private String merchantSecret;

	// The currency used for all transactions, defaults to LKR
	@NotBlank
	private String currency = "LKR";

	// The URL users are redirected to after a successful payment
	@NotBlank
	private String returnUrl;

	// The URL users are redirected to if they cancel the payment
	@NotBlank
	private String cancelUrl;

	// The webhook URL PayHere uses to send server-to-server notifications
	@NotBlank
	private String notifyUrl;

	// Returns the appropriate PayHere checkout URL based on the sandbox setting
	public String getCheckoutUrl() {
		return sandbox
				? "https://sandbox.payhere.lk/pay/checkout"
				: "https://www.payhere.lk/pay/checkout";
	}
}

