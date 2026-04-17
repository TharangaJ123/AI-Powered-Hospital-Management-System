package com.sliit.user_management.controller;

import com.sliit.user_management.dto.JwtResponseDto;
import com.sliit.user_management.dto.LoginRequestDto;
import com.sliit.user_management.security.JwtUtils;
import com.sliit.user_management.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    // Manager responsible for processing authentication requests
    private final AuthenticationManager authenticationManager;
    // Utility class for generating and validating JWT tokens
    private final JwtUtils jwtUtils;

    // Endpoint to authenticate administrators and return a JWT token upon success
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> authenticateAdmin(@RequestBody LoginRequestDto loginRequest) {
        // Authenticate the user with provided email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Generate a JWT token for the authenticated admin
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Retrieve user details from the authentication object
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Extract the user's role from their authorities
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        // Verify that the user has admin privileges
        if (!"ADMIN".equals(role) && !"ROLE_ADMIN".equals(role)) {
            throw new RuntimeException("Unauthorized access to Admin Portal.");
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
