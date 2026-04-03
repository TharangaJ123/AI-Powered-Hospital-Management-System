package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.TelemedicineSessionDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.SessionStatus;
import com.sliit.hospitalManagementSystem.doctor_management.model.TelemedicineSession;
import com.sliit.hospitalManagementSystem.doctor_management.repository.TelemedicineSessionRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TelemedicineSessionService {

    private final TelemedicineSessionRepository telemedicineSessionRepository;

    public TelemedicineSessionService(TelemedicineSessionRepository telemedicineSessionRepository) {
        this.telemedicineSessionRepository = telemedicineSessionRepository;
    }

    public TelemedicineSessionDTO createSession(TelemedicineSessionDTO dto) {
        TelemedicineSession session = mapToEntity(dto);

        if (session.getSessionUrl() == null || session.getSessionUrl().isBlank()) {
            session.setSessionUrl("https://meet.hospital.com/session/" + UUID.randomUUID().toString());
        }

        return mapToDTO(Objects.requireNonNull(telemedicineSessionRepository.save(session)));
    }

    public TelemedicineSessionDTO getSessionById(@NonNull Long id) {
        return telemedicineSessionRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Telemedicine session not found with id: " + id));
    }

    public List<TelemedicineSessionDTO> getSessionsByDoctorId(Long doctorId) {
        return telemedicineSessionRepository.findByDoctorIdOrderByScheduledStartTimeDesc(doctorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<TelemedicineSessionDTO> getUpcomingSessionsByDoctorId(Long doctorId) {
        return telemedicineSessionRepository.findByDoctorIdAndStatus(doctorId, SessionStatus.SCHEDULED).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TelemedicineSessionDTO startSession(@NonNull Long id) {
        return telemedicineSessionRepository.findById(id)
                .map(session -> {
                    session.setStatus(SessionStatus.IN_PROGRESS);
                    session.setActualStartTime(LocalDateTime.now());
                    return mapToDTO(Objects.requireNonNull(telemedicineSessionRepository.save(session)));
                })
                .orElseThrow(() -> new RuntimeException("Telemedicine session not found with id: " + id));
    }

    public TelemedicineSessionDTO endSession(@NonNull Long id, String notes) {
        return telemedicineSessionRepository.findById(id)
                .map(session -> {
                    session.setStatus(SessionStatus.COMPLETED);
                    session.setActualEndTime(LocalDateTime.now());
                    session.setNotes(notes);
                    return mapToDTO(Objects.requireNonNull(telemedicineSessionRepository.save(session)));
                })
                .orElseThrow(() -> new RuntimeException("Telemedicine session not found with id: " + id));
    }

    public TelemedicineSessionDTO cancelSession(@NonNull Long id) {
        return telemedicineSessionRepository.findById(id)
                .map(session -> {
                    session.setStatus(SessionStatus.CANCELLED);
                    return mapToDTO(Objects.requireNonNull(telemedicineSessionRepository.save(session)));
                })
                .orElseThrow(() -> new RuntimeException("Telemedicine session not found with id: " + id));
    }

    private TelemedicineSessionDTO mapToDTO(@NonNull TelemedicineSession session) {
        return TelemedicineSessionDTO.builder()
                .id(session.getId())
                .doctorId(session.getDoctorId())
                .patientId(session.getPatientId())
                .appointmentRequestId(session.getAppointmentRequestId())
                .sessionUrl(session.getSessionUrl())
                .scheduledStartTime(session.getScheduledStartTime())
                .scheduledEndTime(session.getScheduledEndTime())
                .actualStartTime(session.getActualStartTime())
                .actualEndTime(session.getActualEndTime())
                .status(session.getStatus() != null ? session.getStatus().name() : null)
                .notes(session.getNotes())
                .build();
    }

    private TelemedicineSession mapToEntity(TelemedicineSessionDTO dto) {
        return TelemedicineSession.builder()
                .doctorId(dto.getDoctorId())
                .patientId(dto.getPatientId())
                .appointmentRequestId(dto.getAppointmentRequestId())
                .sessionUrl(dto.getSessionUrl())
                .scheduledStartTime(dto.getScheduledStartTime())
                .scheduledEndTime(dto.getScheduledEndTime())
                .status(SessionStatus.SCHEDULED)
                .build();
    }
}
