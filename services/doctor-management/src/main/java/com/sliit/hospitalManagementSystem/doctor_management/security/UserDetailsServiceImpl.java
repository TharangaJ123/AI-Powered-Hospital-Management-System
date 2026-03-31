package com.sliit.hospitalManagementSystem.doctor_management.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // In this internal service, we trust the JWT subject if the token is valid.
        // We could also check if the doctor profile exists in the local database.
        return new UserDetailsImpl(email);
    }
}
