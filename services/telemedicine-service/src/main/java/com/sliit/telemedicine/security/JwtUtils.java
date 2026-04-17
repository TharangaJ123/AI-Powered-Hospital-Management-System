package com.sliit.telemedicine.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;

@Component
public class JwtUtils {

    // Secret key used for signing and verifying JWT tokens
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    // Decodes the JWT token and retrieves the subject (username/email)
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Extracts the roles list from the JWT token's claims
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromJwtToken(String token) {
        return (List<String>) Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().get("roles");
    }

    // Validates the JWT token by checking its signature and expiration
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            // Token validation failed (expired, tampered, etc.)
        }
        return false;
    }

    // Converts the secret string into a cryptographic key suitable for HMAC-SHA algorithms
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
