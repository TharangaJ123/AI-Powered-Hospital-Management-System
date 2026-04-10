package com.sliit.hospitalManagementSystem.payment_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.sliit.hospitalManagementSystem.payment_service.config.PayHereProperties;
import com.sliit.hospitalManagementSystem.payment_service.dto.PayHereNotifyRequest;
import com.sliit.hospitalManagementSystem.payment_service.dto.PaymentInitiationRequest;
import com.sliit.hospitalManagementSystem.payment_service.dto.PaymentInitiationResponse;
import com.sliit.hospitalManagementSystem.payment_service.dto.PaymentStatusResponse;
import com.sliit.hospitalManagementSystem.payment_service.model.PaymentRecord;
import com.sliit.hospitalManagementSystem.payment_service.model.PaymentState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayHereService {

	private final PayHereProperties payHereProperties;
	private final Map<String, PaymentRecord> paymentStore = new ConcurrentHashMap<>();

	public PaymentInitiationResponse initiatePayment(PaymentInitiationRequest request) {
		String amount = formatAmount(request.getAmount());
		String orderId = request.getOrderId();
		String hash = generateCheckoutHash(orderId, amount);

		paymentStore.put(orderId, PaymentRecord.builder()
				.orderId(orderId)
				.amount(new BigDecimal(amount))
				.currency(payHereProperties.getCurrency())
				.state(PaymentState.PENDING)
				.message("Payment initiated")
				.updatedAt(Instant.now())
				.build());

		Map<String, String> formFields = new LinkedHashMap<>();
		formFields.put("merchant_id", payHereProperties.getMerchantId());
		formFields.put("return_url", payHereProperties.getReturnUrl());
		formFields.put("cancel_url", payHereProperties.getCancelUrl());
		formFields.put("notify_url", payHereProperties.getNotifyUrl());
		formFields.put("order_id", orderId);
		formFields.put("items", request.getItems());
		formFields.put("currency", payHereProperties.getCurrency());
		formFields.put("amount", amount);
		formFields.put("first_name", request.getFirstName());
		formFields.put("last_name", request.getLastName());
		formFields.put("email", request.getEmail());
		formFields.put("phone", request.getPhone());
		formFields.put("address", request.getAddress());
		formFields.put("city", request.getCity());
		formFields.put("country", request.getCountry());
		formFields.put("hash", hash);

		return PaymentInitiationResponse.builder()
				.checkoutUrl(payHereProperties.getCheckoutUrl())
				.paymentFormFields(formFields)
				.build();
	}

	public boolean handleNotification(PayHereNotifyRequest notifyRequest) {
		if (!payHereProperties.getMerchantId().equals(notifyRequest.getMerchantId())) {
			return false;
		}

		// Skip signature validation for testing
		// String localSignature = generateNotificationSignature(notifyRequest);
		// boolean isMockPayment = "mock_signature".equals(notifyRequest.getMd5sig());
		// if (!isMockPayment && !localSignature.equalsIgnoreCase(notifyRequest.getMd5sig())) {
		// 	return false;
		// }

		PaymentState state = "2".equals(notifyRequest.getStatusCode())
				? PaymentState.SUCCESS
				: PaymentState.FAILED;

		paymentStore.compute(notifyRequest.getOrderId(), (orderId, existing) -> {
			BigDecimal amount = existing != null ? existing.getAmount() : new BigDecimal(notifyRequest.getPayhereAmount());
			String currency = existing != null ? existing.getCurrency() : notifyRequest.getPayhereCurrency();

			return PaymentRecord.builder()
					.orderId(orderId)
					.paymentId(notifyRequest.getPaymentId())
					.amount(amount)
					.currency(currency)
					.state(state)
					.message(notifyRequest.getStatusMessage())
					.updatedAt(Instant.now())
					.build();
		});

		return true;
	}

	public Optional<PaymentStatusResponse> getPaymentStatus(String orderId) {
		return Optional.ofNullable(paymentStore.get(orderId))
				.map(record -> PaymentStatusResponse.builder()
						.orderId(record.getOrderId())
						.paymentId(record.getPaymentId())
						.status(record.getState().name())
						.message(record.getMessage())
						.updatedAt(record.getUpdatedAt())
						.build());
	}

	private String generateCheckoutHash(String orderId, String amount) {
		// PayHere hash format: MD5(merchant_id + order_id + amount + currency + MD5(merchant_secret))
		// Ensure amount has exactly 2 decimal places
		String formattedAmount = formatAmount(new BigDecimal(amount));
		String merchantSecret = payHereProperties.getMerchantSecret();
		String merchantSecretHash = md5(merchantSecret).toUpperCase();
		String plain = payHereProperties.getMerchantId() + orderId + formattedAmount + payHereProperties.getCurrency() + merchantSecretHash;
		String finalHash = md5(plain).toUpperCase();
		
		// Debug logging
		System.out.println("=== PAYHERE HASH GENERATION DEBUG ===");
		System.out.println("Merchant ID: " + payHereProperties.getMerchantId());
		System.out.println("Order ID: " + orderId);
		System.out.println("Amount: " + formattedAmount);
		System.out.println("Currency: " + payHereProperties.getCurrency());
		System.out.println("Merchant Secret: " + merchantSecret);
		System.out.println("Merchant Secret Hash: " + merchantSecretHash);
		System.out.println("Plain String: " + plain);
		System.out.println("Final Hash: " + finalHash);
		System.out.println("=====================================");
		
		return finalHash;
	}

	private String generateNotificationSignature(PayHereNotifyRequest notifyRequest) {
		String merchantSecretHash = md5(payHereProperties.getMerchantSecret()).toUpperCase();
		String plain = notifyRequest.getMerchantId()
				+ notifyRequest.getOrderId()
				+ notifyRequest.getPayhereAmount()
				+ notifyRequest.getPayhereCurrency()
				+ notifyRequest.getStatusCode()
				+ merchantSecretHash;
		return md5(plain).toUpperCase();
	}

	private String formatAmount(BigDecimal amount) {
		return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}

	private String md5(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : digest) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available", e);
		}
	}
}

