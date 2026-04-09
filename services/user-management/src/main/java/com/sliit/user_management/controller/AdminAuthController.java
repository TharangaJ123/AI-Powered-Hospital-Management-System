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

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> authenticateAdmin(@RequestBody LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        if (!"ADMIN".equals(role) && !"ROLE_ADMIN".equals(role)) {
            throw new RuntimeException("Unauthorized access to Admin Portal.");
        }

        return ResponseEntity.ok(JwtResponseDto.builder()
                .token(jwt)
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .name(userDetails.getName())
                .role(role)
                .build());
    }
}
