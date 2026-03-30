package com.sliit.hospitalManagementSystem.doctor_management.repository;

import com.sliit.hospitalManagementSystem.doctor_management.model.AppointmentRequest;
import com.sliit.hospitalManagementSystem.doctor_management.model.AppointmentRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRequestRepository extends JpaRepository<AppointmentRequest, Long> {

    List<AppointmentRequest> findByDoctorId(Long doctorId);

    List<AppointmentRequest> findByDoctorIdAndStatus(Long doctorId, AppointmentRequestStatus status);

    List<AppointmentRequest> findByPatientId(Long patientId);

    List<AppointmentRequest> findByDoctorIdOrderByRequestedDateTimeDesc(Long doctorId);
}
