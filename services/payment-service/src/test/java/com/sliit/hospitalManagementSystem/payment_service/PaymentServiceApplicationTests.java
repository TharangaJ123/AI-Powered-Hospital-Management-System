package com.sliit.hospitalManagementSystem.payment_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"payhere.merchant-id=test-merchant",
		"payhere.merchant-secret=test-secret",
		"payhere.currency=LKR",
		"payhere.return-url=http://localhost:3000/payment/success",
		"payhere.cancel-url=http://localhost:3000/payment/cancel",
		"payhere.notify-url=http://localhost:8084/api/payments/payhere/notify"
})
class PaymentServiceApplicationTests {

	@Test
	void contextLoads() {
	}
}

