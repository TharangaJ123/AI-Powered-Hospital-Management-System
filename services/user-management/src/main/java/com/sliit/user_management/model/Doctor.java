package com.sliit.user_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "doctors")
@DiscriminatorValue("DOCTOR")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Doctor extends User {

    // Unique registration or license number issued by a medical board
    @Column(unique = true)
    private String doctorRegistrationNumber;

    // The doctor's medical area of expertise (e.g., Cardiology, Pediatrics)
    private String specialization;
}
