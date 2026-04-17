package com.sliit.contact_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// Main entry point for the Contact microservice handling user inquiries
@SpringBootApplication
@EnableDiscoveryClient
public class ContactServiceApplication {

    // Bootstraps the application and enables service discovery via Eureka
    public static void main(String[] args) {
        SpringApplication.run(ContactServiceApplication.class, args);
    }
}
