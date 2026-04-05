package com.sliit.user_management.security;

import com.sliit.user_management.model.Role;
import com.sliit.user_management.model.User;
import com.sliit.user_management.repository.PatientProfileRepository;
import com.sliit.user_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        String name = user.getEmail(); // Default to email
        if (user.getRole() == Role.PATIENT) {
            name = patientProfileRepository.findByUserId(user.getId())
                    .map(p -> p.getFirstName() + " " + p.getLastName())
                    .orElse(user.getEmail());
        }

        return UserDetailsImpl.build(user, name);
    }
}
