package com.sliit.hospitalManagementSystem.doctor_management.repository;

import com.sliit.hospitalManagementSystem.doctor_management.model.AvailabilitySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface AvailabilityScheduleRepository extends JpaRepository<AvailabilitySchedule, Long> {

    List<AvailabilitySchedule> findByDoctorId(Long doctorId);

    List<AvailabilitySchedule> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);

    List<AvailabilitySchedule> findByDoctorIdAndIsAvailable(Long doctorId, Boolean isAvailable);

    void deleteByDoctorId(Long doctorId);
}
