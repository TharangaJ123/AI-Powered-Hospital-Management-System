package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.AvailabilityScheduleDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.AvailabilitySchedule;
import com.sliit.hospitalManagementSystem.doctor_management.model.ConsultationType;
import com.sliit.hospitalManagementSystem.doctor_management.repository.AvailabilityScheduleRepository;
import org.springframework.lang.NonNull;
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

    // Creates a new recurring availability slot for a doctor
    @SuppressWarnings("null")
    public AvailabilityScheduleDTO createSchedule(AvailabilityScheduleDTO dto) {
        AvailabilitySchedule schedule = mapToEntity(dto);
        return mapToDTO(availabilityScheduleRepository.save(schedule));
    }

    // Retrieves all configured availability slots for a specific doctor
    public List<AvailabilityScheduleDTO> getSchedulesByDoctorId(Long doctorId) {
        return availabilityScheduleRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Fetches a doctor's availability for a specific day of the week
    public List<AvailabilityScheduleDTO> getSchedulesByDoctorIdAndDay(Long doctorId, DayOfWeek dayOfWeek) {
        return availabilityScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Retrieves only the slots that are currently marked as active/available for booking
    public List<AvailabilityScheduleDTO> getAvailableSchedules(Long doctorId) {
        return availabilityScheduleRepository.findByDoctorIdAndIsAvailable(doctorId, true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Updates an existing availability slot's details such as time or slot duration
    public AvailabilityScheduleDTO updateSchedule(@NonNull Long id, AvailabilityScheduleDTO dto) {
        return availabilityScheduleRepository.findById(id)
                .map(existing -> {
                    existing.setDayOfWeek(dto.getDayOfWeek());
                    existing.setStartTime(dto.getStartTime());
                    existing.setEndTime(dto.getEndTime());
                    existing.setSlotDurationMinutes(dto.getSlotDurationMinutes());
                    existing.setMaxPatientsPerSlot(dto.getMaxPatientsPerSlot());
                    existing.setIsAvailable(dto.getIsAvailable());

                    if (dto.getConsultationType() != null) {
                        existing.setConsultationType(ConsultationType.valueOf(dto.getConsultationType().toUpperCase()));
                    }

                    return mapToDTO(availabilityScheduleRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Availability schedule not found with id: " + id));
    }

    // Removes a specific availability slot by ID
    public void deleteSchedule(@NonNull Long id) {
        if (!availabilityScheduleRepository.existsById(id)) {
            throw new RuntimeException("Availability schedule not found with id: " + id);
        }
        availabilityScheduleRepository.deleteById(id);
    }

    // Transactional method to wipe all availability configurations for a doctor
    @Transactional
    public void deleteAllSchedulesByDoctorId(Long doctorId) {
        availabilityScheduleRepository.deleteByDoctorId(doctorId);
    }

    // Converts an AvailabilitySchedule JPA entity into a DTO for API responses
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

    // Converts an AvailabilityScheduleDTO into a JPA entity, applying default values where necessary
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
