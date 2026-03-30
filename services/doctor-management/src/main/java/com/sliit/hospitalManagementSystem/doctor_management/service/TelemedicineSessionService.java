package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.TelemedicineSessionDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.SessionStatus;
import com.sliit.hospitalManagementSystem.doctor_management.model.TelemedicineSession;
import com.sliit.hospitalManagementSystem.doctor_management.repository.TelemedicineSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

        // Generate a unique session URL if not provided
        if (session.getSessionUrl() == null || session.getSessionUrl().isBlank()) {
            session.setSessionUrl("https://meet.hospital.com/session/" + UUID.randomUUID().toString());
        }

        TelemedicineSession saved = telemedicineSessionRepository.save(session);
        return mapToDTO(saved);
    }

    public TelemedicineSessionDTO getSessionById(Long id) {
        TelemedicineSession session = telemedicineSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Telemedicine session not found with id: " + id));
        return mapToDTO(session);
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

    public TelemedicineSessionDTO startSession(Long id) {
        TelemedicineSession session = telemedicineSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Telemedicine session not found with id: " + id));

        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setActualStartTime(LocalDateTime.now());

        TelemedicineSession updated = telemedicineSessionRepository.save(session);
        return mapToDTO(updated);
    }

    public TelemedicineSessionDTO endSession(Long id, String notes) {
        TelemedicineSession session = telemedicineSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Telemedicine session not found with id: " + id));

        session.setStatus(SessionStatus.COMPLETED);
        session.setActualEndTime(LocalDateTime.now());
        session.setNotes(notes);

        TelemedicineSession updated = telemedicineSessionRepository.save(session);
        return mapToDTO(updated);
    }

    public TelemedicineSessionDTO cancelSession(Long id) {
        TelemedicineSession session = telemedicineSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Telemedicine session not found with id: " + id));

        session.setStatus(SessionStatus.CANCELLED);

        TelemedicineSession updated = telemedicineSessionRepository.save(session);
        return mapToDTO(updated);
    }

    // --- Mapping helpers ---

    private TelemedicineSessionDTO mapToDTO(TelemedicineSession session) {
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
