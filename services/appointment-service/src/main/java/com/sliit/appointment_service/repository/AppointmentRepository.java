package com.sliit.appointment_service.repository;

import com.sliit.appointment_service.model.Appointment;
import com.sliit.appointment_service.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Fetches all appointments associated with a specific patient
    List<Appointment> findByPatientId(Long patientId);
    // Fetches all appointments assigned to a specific doctor
    List<Appointment> findByDoctorId(Long doctorId);
    // Checks if a duplicate appointment exists with the same patient, doctor, and time
    boolean existsByPatientIdAndDoctorIdAndAppointmentDateAndStatus(
            Long patientId,
            Long doctorId,
            LocalDateTime appointmentDate,
            AppointmentStatus status
    );

    // Checks for doctor availability by searching for overlapping appointments within a time range
    boolean existsByDoctorIdAndAppointmentDateBetweenAndStatusIn(
        Long doctorId,
        LocalDateTime start,
        LocalDateTime end,
        List<AppointmentStatus> statuses
    );
}
