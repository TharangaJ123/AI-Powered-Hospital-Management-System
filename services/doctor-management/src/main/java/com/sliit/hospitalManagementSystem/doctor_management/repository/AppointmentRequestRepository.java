package com.sliit.hospitalManagementSystem.doctor_management.repository;

import com.sliit.hospitalManagementSystem.doctor_management.model.AppointmentRequest;
import com.sliit.hospitalManagementSystem.doctor_management.model.AppointmentRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRequestRepository extends JpaRepository<AppointmentRequest, Long> {

    // Retrieves all appointment requests assigned to a specific doctor
    List<AppointmentRequest> findByDoctorId(Long doctorId);

    // Filters appointment requests for a doctor based on their current status (e.g., PENDING)
    List<AppointmentRequest> findByDoctorIdAndStatus(Long doctorId, AppointmentRequestStatus status);

    // Retrieves all appointment requests submitted by a specific patient
    List<AppointmentRequest> findByPatientId(Long patientId);

    // Retrieves all requests for a doctor, sorted by the most recent requested time first
    List<AppointmentRequest> findByDoctorIdOrderByRequestedDateTimeDesc(Long doctorId);
}
