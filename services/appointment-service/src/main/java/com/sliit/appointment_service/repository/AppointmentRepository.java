package com.sliit.appointment_service.repository;

import com.sliit.appointment_service.model.Appointment;
import com.sliit.appointment_service.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    boolean existsByPatientIdAndDoctorIdAndStatus(Long patientId, Long doctorId, AppointmentStatus status);
}
