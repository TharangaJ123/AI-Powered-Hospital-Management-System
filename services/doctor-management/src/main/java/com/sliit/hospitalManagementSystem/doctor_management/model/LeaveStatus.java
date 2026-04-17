package com.sliit.hospitalManagementSystem.doctor_management.model;

public enum LeaveStatus {
    // Leave request is awaiting administrative decision
    PENDING,
    // Absence has been authorized and system availability will be updated
    APPROVED,
    // Absence request was not authorized
    REJECTED
}
