package com.sliit.user_management.service;

import com.sliit.user_management.dto.UserRegistrationDto;
import com.sliit.user_management.dto.UserResponseDto;
import com.sliit.user_management.model.PatientProfile;
import com.sliit.user_management.model.Role;
import com.sliit.user_management.model.User;
import com.sliit.user_management.repository.PatientProfileRepository;
import com.sliit.user_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto registerByRole(UserRegistrationDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }
        Role role = request.getRole() == null ? Role.PATIENT : request.getRole();

        if (role == Role.DOCTOR && request.getDoctorRegistrationNumber() != null) {
            if (userRepository.findByDoctorRegistrationNumber(request.getDoctorRegistrationNumber()).isPresent()) {
                throw new RuntimeException("Doctor Registration Number already in use");
            }
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .active(role != Role.DOCTOR)
                .isVerified(role != Role.DOCTOR)
                .doctorRegistrationNumber(role == Role.DOCTOR ? request.getDoctorRegistrationNumber() : null)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        user = userRepository.save(user);

        if (role == Role.PATIENT) {
            PatientProfile profile = PatientProfile.builder()
                    .user(user)
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phoneNumber(request.getPhoneNumber())
                    .address(request.getAddress())
                    .dateOfBirth(request.getDateOfBirth())
                    .build();
            patientProfileRepository.save(profile);
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .doctorRegistrationNumber(user.getDoctorRegistrationNumber())
                .build();
    }
}
