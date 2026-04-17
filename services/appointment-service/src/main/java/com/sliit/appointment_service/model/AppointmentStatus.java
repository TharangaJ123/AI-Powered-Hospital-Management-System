package com.sliit.appointment_service.model;

public enum AppointmentStatus {
    // Initial state after a patient successfully books a slot
    BOOKED,
    // Doctor has reviewed and confirmed the appointment
    ACCEPTED,
    // Doctor has declined the appointment request
    REJECTED,
    // Appointment was revoked by the patient or administrator
    CANCELLED,
    // The consultation was successfully conducted
    COMPLETED
}
