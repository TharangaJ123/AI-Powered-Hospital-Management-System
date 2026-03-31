package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.AvailabilityScheduleDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.AvailabilitySchedule;
import com.sliit.hospitalManagementSystem.doctor_management.model.ConsultationType;
import com.sliit.hospitalManagementSystem.doctor_management.repository.AvailabilityScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityScheduleService {

    private final AvailabilityScheduleRepository availabilityScheduleRepository;

    public AvailabilityScheduleService(AvailabilityScheduleRepository availabilityScheduleRepository) {
        this.availabilityScheduleRepository = availabilityScheduleRepository;
    }

    public AvailabilityScheduleDTO createSchedule(AvailabilityScheduleDTO dto) {
        AvailabilitySchedule schedule = mapToEntity(dto);
        AvailabilitySchedule saved = availabilityScheduleRepository.save(schedule);
        return mapToDTO(saved);
    }

    public List<AvailabilityScheduleDTO> getSchedulesByDoctorId(Long doctorId) {
        return availabilityScheduleRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AvailabilityScheduleDTO> getSchedulesByDoctorIdAndDay(Long doctorId, DayOfWeek dayOfWeek) {
        return availabilityScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AvailabilityScheduleDTO> getAvailableSchedules(Long doctorId) {
        return availabilityScheduleRepository.findByDoctorIdAndIsAvailable(doctorId, true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AvailabilityScheduleDTO updateSchedule(Long id, AvailabilityScheduleDTO dto) {
        AvailabilitySchedule existing = availabilityScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability schedule not found with id: " + id));

        existing.setDayOfWeek(dto.getDayOfWeek());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setSlotDurationMinutes(dto.getSlotDurationMinutes());
        existing.setMaxPatientsPerSlot(dto.getMaxPatientsPerSlot());
        existing.setIsAvailable(dto.getIsAvailable());

        if (dto.getConsultationType() != null) {
            existing.setConsultationType(ConsultationType.valueOf(dto.getConsultationType().toUpperCase()));
        }

        AvailabilitySchedule updated = availabilityScheduleRepository.save(existing);
        return mapToDTO(updated);
    }

    public void deleteSchedule(Long id) {
        if (!availabilityScheduleRepository.existsById(id)) {
            throw new RuntimeException("Availability schedule not found with id: " + id);
        }
        availabilityScheduleRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllSchedulesByDoctorId(Long doctorId) {
        availabilityScheduleRepository.deleteByDoctorId(doctorId);
    }

    // --- Mapping helpers ---

    private AvailabilityScheduleDTO mapToDTO(AvailabilitySchedule schedule) {
        return AvailabilityScheduleDTO.builder()
                .id(schedule.getId())
                .doctorId(schedule.getDoctorId())
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .slotDurationMinutes(schedule.getSlotDurationMinutes())
                .maxPatientsPerSlot(schedule.getMaxPatientsPerSlot())
                .isAvailable(schedule.getIsAvailable())
                .consultationType(schedule.getConsultationType() != null ? schedule.getConsultationType().name() : null)
                .build();
    }

    private AvailabilitySchedule mapToEntity(AvailabilityScheduleDTO dto) {
        return AvailabilitySchedule.builder()
                .doctorId(dto.getDoctorId())
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .slotDurationMinutes(dto.getSlotDurationMinutes() != null ? dto.getSlotDurationMinutes() : 30)
                .maxPatientsPerSlot(dto.getMaxPatientsPerSlot() != null ? dto.getMaxPatientsPerSlot() : 1)
                .isAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true)
                .consultationType(dto.getConsultationType() != null ?
                        ConsultationType.valueOf(dto.getConsultationType().toUpperCase()) : ConsultationType.BOTH)
                .build();
    }
}
