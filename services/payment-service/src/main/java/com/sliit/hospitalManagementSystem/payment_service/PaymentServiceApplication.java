package com.sliit.hospitalManagementSystem.payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// Main entry point for the Payment microservice
@SpringBootApplication
@EnableDiscoveryClient
public class PaymentServiceApplication {

	// Starts the Spring Boot application
	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}
}

