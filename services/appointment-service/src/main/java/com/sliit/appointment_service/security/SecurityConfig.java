package com.sliit.appointment_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Handler for unauthorized access attempts
    private final AuthEntryPointJwt unauthorizedHandler;
    // Filter for validating JWT tokens in incoming requests
    private final AuthTokenFilter authenticationJwtTokenFilter;

    // Defines the security filter chain and authorization policies for HTTP requests
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF as the service is stateless and uses JWT
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Register the unauthorized entry point
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Enforce stateless session management
            .authorizeHttpRequests(auth -> 
                // Define public and protected endpoints
                auth.requestMatchers(org.springframework.http.HttpMethod.POST, "/api/appointments").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/appointments/availability").permitAll()
                    .requestMatchers("/api/appointments/**").authenticated()
                    .anyRequest().permitAll()
            );

        // Add the JWT authentication filter before the standard username/password filter
        http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
