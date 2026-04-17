package com.sliit.telemedicine.repository;

import com.sliit.telemedicine.model.TelemedicineSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelemedicineSessionRepository extends JpaRepository<TelemedicineSession, Long> {
    // Custom query to find a telemedicine session by its linked appointment ID
    Optional<TelemedicineSession> findByAppointmentId(Long appointmentId);
}
