package com.sliit.hospitalManagementSystem.doctor_management.model;

public enum DoctorStatus {
    // Newly registered profile waiting for administrator verification
    PENDING_APPROVAL,
    // Verified and fully operational within the hospital system
    ACTIVE,
    // Profile is temporarily or permanently disabled from booking
    INACTIVE,
    // Doctor is currently away and not taking any appointments
    ON_LEAVE,
    // Account restricted by an administrator due to policy violations
    SUSPENDED
}
