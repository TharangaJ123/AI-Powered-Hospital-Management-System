package com.sliit.hospitalManagementSystem.doctor_management.model;

public enum SessionStatus {
    // Session is confirmed and waiting for the scheduled time
    SCHEDULED,
    // The doctor and patient are currently in the virtual meeting room
    IN_PROGRESS,
    // The consultation has been successfully concluded
    COMPLETED,
    // The session was terminated before it could occur
    CANCELLED,
    // The patient failed to join the virtual session at the scheduled time
    NO_SHOW
}
