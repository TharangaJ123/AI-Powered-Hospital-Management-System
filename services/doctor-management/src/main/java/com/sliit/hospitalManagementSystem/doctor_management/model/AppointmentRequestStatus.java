package com.sliit.hospitalManagementSystem.doctor_management.model;

public enum AppointmentRequestStatus {
    // The request has been submitted by the patient and is awaiting doctor review
    PENDING,
    // The doctor has reviewed and confirmed the appointment slot
    ACCEPTED,
    // The doctor has declined the appointment request
    REJECTED,
    // The appointment was revoked by either the patient or an administrator
    CANCELLED,
    // The medical consultation has been successfully conducted
    COMPLETED
}
