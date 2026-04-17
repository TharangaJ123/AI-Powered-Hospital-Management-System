package com.sliit.hospitalManagementSystem.doctor_management.repository;

import com.sliit.hospitalManagementSystem.doctor_management.model.PatientReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientReportRepository extends JpaRepository<PatientReport, Long> {

    List<PatientReport> findByDoctorId(Long doctorId);

    List<PatientReport> findByPatientId(Long patientId);

    List<PatientReport> findByDoctorIdAndPatientId(Long doctorId, Long patientId);

    List<PatientReport> findByDoctorIdOrderByUploadedAtDesc(Long doctorId);
}
