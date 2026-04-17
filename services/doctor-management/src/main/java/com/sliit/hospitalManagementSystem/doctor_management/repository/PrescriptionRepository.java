package com.sliit.hospitalManagementSystem.doctor_management.repository;

import com.sliit.hospitalManagementSystem.doctor_management.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // Retrieves all prescriptions issued by a specific doctor
    List<Prescription> findByDoctorId(Long doctorId);

    // Retrieves the historical list of all prescriptions assigned to a specific patient
    List<Prescription> findByPatientId(Long patientId);

    // Fetches prescriptions issued by a specific doctor to a specific patient
    List<Prescription> findByDoctorIdAndPatientId(Long doctorId, Long patientId);

    // Lists prescriptions for a doctor, sorted by the most recently created ones
    List<Prescription> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);
}
