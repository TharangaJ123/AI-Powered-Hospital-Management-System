package com.sliit.hospitalManagementSystem.ai_symptom_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AiSymptomServiceApplication {

	// Main entry point for the Spring Boot AI Symptom Checker Microservice
	public static void main(String[] args) {
		SpringApplication.run(AiSymptomServiceApplication.class, args);
	}

}
