package com.sliit.hospitalManagementSystem.discovery_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// Entry point for the Eureka Discovery Server, which maintains a registry of all active microservices
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

	// Bootstraps the discovery server to allow other services to register and discover each other
	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServiceApplication.class, args);
	}

}
