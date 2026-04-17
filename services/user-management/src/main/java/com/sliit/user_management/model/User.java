package com.sliit.user_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class User {

    // Primary key for the user, automatically generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique email address used for login
    @Column(unique = true, nullable = false)
    private String email;

    // Encrypted password for user authentication
    @Column(nullable = false)
    private String password;

    // Security role assigned to the user (e.g., ADMIN, PATIENT, DOCTOR)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Flag indicating if the account is currently active
    @Builder.Default
    private boolean active = true;

    // Flag indicating if the user's identity or credentials have been verified
    @Builder.Default
    private boolean isVerified = false;

    // User's first name
    private String firstName;
    // User's last name
    private String lastName;
}
