package com.sliit.user_management.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "patients")
@DiscriminatorValue("PATIENT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Patient extends User {
    // Patient's contact phone number
    private String phoneNumber;
    // Patient's residential or mailing address
    private String address;
    // Patient's date of birth in string format (e.g., YYYY-MM-DD)
    private String dateOfBirth;
}
