package com.sliit.appointment_service.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// Implementation of Spring Security's UserDetails to represent an authenticated user in the microservice
public class UserDetailsImpl implements UserDetails {
    // Unique identifier for the user (email)
    private String email;
    // Set of granted authorities/roles for the user
    private Collection<? extends GrantedAuthority> authorities;

    // Constructor to initialize user details with email and authorities
    public UserDetailsImpl(String email, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.authorities = authorities;
    }

    // Returns the roles assigned to the user
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Password is not used in stateless inter-service communication
    @Override
    public String getPassword() {
        return null;
    }

    // Returns the unique username (email) for identification
    @Override
    public String getUsername() {
        return email;
    }

    // Indicates that the account is perpetually valid for this stateless service
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indicates that the account is never locked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Indicates that credentials remain valid
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Indicates that the user account is active
    @Override
    public boolean isEnabled() {
        return true;
    }
}
