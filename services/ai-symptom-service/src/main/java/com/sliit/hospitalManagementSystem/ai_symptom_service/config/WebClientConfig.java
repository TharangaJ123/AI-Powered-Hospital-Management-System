package com.sliit.hospitalManagementSystem.ai_symptom_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    // Configures and provides a WebClient.Builder bean for making HTTP calls
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
