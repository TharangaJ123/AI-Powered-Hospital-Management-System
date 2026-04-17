package com.sliit.hospitalManagementSystem.doctor_management.repository;

import com.sliit.hospitalManagementSystem.doctor_management.model.AvailabilitySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface AvailabilityScheduleRepository extends JpaRepository<AvailabilitySchedule, Long> {

    // Fetches the complete weekly availability configuration for a specific doctor
    List<AvailabilitySchedule> findByDoctorId(Long doctorId);

    // Retrieves availability slots for a doctor on a specific day (e.g., all Monday slots)
    List<AvailabilitySchedule> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);

    // Filters a doctor's availability slots based on whether they are currently active/inactive
    List<AvailabilitySchedule> findByDoctorIdAndIsAvailable(Long doctorId, Boolean isAvailable);

    // Bulk deletes all availability configuration records for a single doctor
    void deleteByDoctorId(Long doctorId);
}
