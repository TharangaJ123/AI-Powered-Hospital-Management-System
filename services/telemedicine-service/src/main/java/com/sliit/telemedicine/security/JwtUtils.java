package com.sliit.telemedicine.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;

@Component
public class JwtUtils {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromJwtToken(String token) {
        return (List<String>) Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().get("roles");
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            // log error
        }
        return false;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
