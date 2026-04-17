package com.sliit.hospitalManagementSystem.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// Main entry point for the API Gateway, acting as the central entry for all microservices
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

	// Bootstraps the Spring Boot application and registers with the discovery server
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
