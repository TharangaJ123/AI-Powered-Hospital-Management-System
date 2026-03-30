package com.sliit.hospitalManagementSystem.doctor_management.repository;

import com.sliit.hospitalManagementSystem.doctor_management.model.SessionStatus;
import com.sliit.hospitalManagementSystem.doctor_management.model.TelemedicineSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemedicineSessionRepository extends JpaRepository<TelemedicineSession, Long> {

    List<TelemedicineSession> findByDoctorId(Long doctorId);

    List<TelemedicineSession> findByDoctorIdAndStatus(Long doctorId, SessionStatus status);

    List<TelemedicineSession> findByPatientId(Long patientId);

    List<TelemedicineSession> findByDoctorIdOrderByScheduledStartTimeDesc(Long doctorId);
}
