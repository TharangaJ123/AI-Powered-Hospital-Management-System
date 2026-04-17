package com.sliit.hospitalManagementSystem.notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// Entry point for the Notification microservice, responsible for Email and SMS dispatch
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

	// Bootstraps the Spring Boot application and joins the service discovery network
	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
