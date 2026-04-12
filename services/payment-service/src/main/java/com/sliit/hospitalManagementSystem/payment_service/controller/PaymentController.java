package com.sliit.hospitalManagementSystem.payment_service.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sliit.hospitalManagementSystem.payment_service.dto.PayHereNotifyRequest;
import com.sliit.hospitalManagementSystem.payment_service.dto.PaymentInitiationRequest;
import com.sliit.hospitalManagementSystem.payment_service.dto.PaymentInitiationResponse;
import com.sliit.hospitalManagementSystem.payment_service.dto.PaymentStatusResponse;
import com.sliit.hospitalManagementSystem.payment_service.service.PayHereService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentController {

	private final PayHereService payHereService;

	@PostMapping("/payhere/initiate")
	public ResponseEntity<PaymentInitiationResponse> initiatePayment(
			@Valid @RequestBody PaymentInitiationRequest request) {
		return ResponseEntity.ok(payHereService.initiatePayment(request));
	}

	@PostMapping(value = "/payhere/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<String> handlePayHereNotification(@ModelAttribute PayHereNotifyRequest notifyRequest) {
		boolean valid = payHereService.handleNotification(notifyRequest);
		return valid ? ResponseEntity.ok("OK") : ResponseEntity.badRequest().body("INVALID_SIGNATURE");
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<PaymentStatusResponse> getPaymentStatus(@PathVariable String orderId) {
		return payHereService.getPaymentStatus(orderId)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
}

