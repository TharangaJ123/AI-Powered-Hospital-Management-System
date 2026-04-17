package com.sliit.telemedicine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// Main entry point for the Telemedicine microservice
@SpringBootApplication
@EnableDiscoveryClient
public class TelemedicineServiceApplication {
    // Starts the Spring Boot application
    public static void main(String[] args) {
        SpringApplication.run(TelemedicineServiceApplication.class, args);
    }
}
