package com.sliit.appointment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Main entry point for the Appointment microservice
@SpringBootApplication
public class AppointmentServiceApplication {

	// Starts the Spring Boot application
	public static void main(String[] args) {
		SpringApplication.run(AppointmentServiceApplication.class, args);
	}

}
