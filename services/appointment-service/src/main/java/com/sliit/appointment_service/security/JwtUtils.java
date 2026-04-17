package com.sliit.appointment_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;

// Component for handling JWT operations such as parsing, claim extraction, and validation
@Component
public class JwtUtils {

    // Secret key for signing and verifying tokens, injected from configuration
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    // Generates a cryptographic key from the secret string
    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Extracts the subject (username/email) from a validated JWT token
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
               .parseClaimsJws(token).getBody().getSubject();
    }

    // Extracts the user roles list from the custom "roles" claim in the JWT body
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromJwtToken(String token) {
        return (List<String>) Jwts.parserBuilder().setSigningKey(key()).build()
               .parseClaimsJws(token).getBody().get("roles");
    }

    // Verifies the integrity and expiration status of a JWT token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }
}
