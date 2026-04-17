package com.sliit.hospitalManagementSystem.doctor_management.repository;

import com.sliit.hospitalManagementSystem.doctor_management.model.SessionStatus;
import com.sliit.hospitalManagementSystem.doctor_management.model.TelemedicineSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemedicineSessionRepository extends JpaRepository<TelemedicineSession, Long> {

    // Retrieves all telemedicine sessions assigned to a specific doctor
    List<TelemedicineSession> findByDoctorId(Long doctorId);

    // Filters virtual sessions for a doctor by their current state (e.g., IN_PROGRESS, SCHEDULED)
    List<TelemedicineSession> findByDoctorIdAndStatus(Long doctorId, SessionStatus status);

    // Retrieves the historical list of virtual consultations for a specific patient
    List<TelemedicineSession> findByPatientId(Long patientId);

    // Lists a doctor's sessions sorted by the planned start time in descending order
    List<TelemedicineSession> findByDoctorIdOrderByScheduledStartTimeDesc(Long doctorId);
}
