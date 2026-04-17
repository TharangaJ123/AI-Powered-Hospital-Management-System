package com.sliit.telemedicine.config;

import com.sliit.telemedicine.security.AuthTokenFilter;
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

    // Custom JWT filter for validating tokens in incoming requests
    private final AuthTokenFilter authenticationJwtTokenFilter;

    // Configures the security filter chain to handle CSRF, CORS, and endpoint authorization
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disabling CSRF protection as the API is stateless
            .cors(org.springframework.security.config.Customizer.withDefaults()) // Enabling default CORS settings
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Setting session to stateless
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // Allowing preflight OPTIONS requests
                    .requestMatchers("/api/telemedicine/**").authenticated() // Requiring authentication for telemedicine API endpoints
                    .anyRequest().permitAll() // Allowing all other requests (e.g., actuator, discovery)
            );

        // Adding the custom JWT filter before the standard username/password authentication filter
        http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        // Build and return the configured security filter chain
        return http.build();
    }
}
