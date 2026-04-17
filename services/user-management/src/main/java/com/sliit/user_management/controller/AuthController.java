package com.sliit.user_management.controller;

import com.sliit.user_management.dto.JwtResponseDto;
import com.sliit.user_management.dto.LoginRequestDto;
import com.sliit.user_management.dto.UserRegistrationDto;
import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.security.JwtUtils;
import com.sliit.user_management.security.UserDetailsImpl;
import com.sliit.user_management.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // Manager responsible for processing authentication requests
    private final AuthenticationManager authenticationManager;
    // Utility class for generating and validating JWT tokens
    private final JwtUtils jwtUtils;
    // Service to handle user registration based on roles
    private final RegistrationService registrationService;

    // Endpoint for new users to register in the system
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRegistrationDto registrationRequest) {
        return ResponseEntity.ok(registrationService.registerByRole(registrationRequest));
    }

    // Endpoint to authenticate regular users and return a JWT token
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> authenticateUser(@RequestBody LoginRequestDto loginRequest) {
        // Authenticate the user with provided email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Generate a JWT token for the authenticated user
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Retrieve user details from the authentication object
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Extract the user's role from their authorities
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        // Prevent administrators from logging in through this endpoint
        if ("ADMIN".equals(role) || "ROLE_ADMIN".equals(role)) {
            throw new RuntimeException("Admin login is restricted from this endpoint. Please use the Admin Portal.");
        }

        // Return the JWT token and user details to the client
        return ResponseEntity.ok(JwtResponseDto.builder()
                .token(jwt)
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .name(userDetails.getName())
                .role(role)
                .build());
    }
}
