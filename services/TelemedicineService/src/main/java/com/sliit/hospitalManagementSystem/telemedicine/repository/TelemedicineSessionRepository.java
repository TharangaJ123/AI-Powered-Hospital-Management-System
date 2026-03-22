package com.sliit.hospitalManagementSystem.telemedicine.repository;

import com.sliit.hospitalManagementSystem.telemedicine.model.TelemedicineSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelemedicineSessionRepository extends JpaRepository<TelemedicineSession, Long> {

    Optional<TelemedicineSession> findByAppointmentId(String appointmentId);

    List<TelemedicineSession> findByDoctorId(String doctorId);

    List<TelemedicineSession> findByPatientId(String patientId);

    List<TelemedicineSession> findByStatus(String status);
}
