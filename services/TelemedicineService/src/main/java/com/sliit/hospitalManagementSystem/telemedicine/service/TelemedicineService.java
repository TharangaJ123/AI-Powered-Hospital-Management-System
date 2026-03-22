package com.sliit.hospitalManagementSystem.telemedicine.service;

import com.sliit.hospitalManagementSystem.telemedicine.dto.SessionRequestDTO;
import com.sliit.hospitalManagementSystem.telemedicine.dto.SessionResponseDTO;
import com.sliit.hospitalManagementSystem.telemedicine.model.TelemedicineSession;
import com.sliit.hospitalManagementSystem.telemedicine.repository.TelemedicineSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelemedicineService {

    private static final String JITSI_BASE_URL = "https://meet.jit.si/";

    private final TelemedicineSessionRepository sessionRepository;

    // Create a new telemedicine session and generate a Jitsi Meet URL
    public SessionResponseDTO createSession(SessionRequestDTO request) {
        // Generate a unique room name using appointment ID and UUID
        String roomName = "HMS-" + request.getAppointmentId() + "-" + UUID.randomUUID().toString().substring(0, 8);
        String sessionUrl = JITSI_BASE_URL + roomName;

        TelemedicineSession session = new TelemedicineSession();
        session.setAppointmentId(request.getAppointmentId());
        session.setDoctorId(request.getDoctorId());
        session.setPatientId(request.getPatientId());
        session.setRoomName(roomName);
        session.setSessionUrl(sessionUrl);
        session.setStatus("ACTIVE");
        session.setStartTime(LocalDateTime.now());

        TelemedicineSession saved = sessionRepository.save(session);
        return mapToResponseDTO(saved);
    }

    // Get session by appointment ID
    public SessionResponseDTO getSessionByAppointmentId(String appointmentId) {
        TelemedicineSession session = sessionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Session not found for appointment: " + appointmentId));
        return mapToResponseDTO(session);
    }

    // Get all sessions
    public List<SessionResponseDTO> getAllSessions() {
        return sessionRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Get all sessions for a specific doctor
    public List<SessionResponseDTO> getSessionsByDoctor(String doctorId) {
        return sessionRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Get all sessions for a specific patient
    public List<SessionResponseDTO> getSessionsByPatient(String patientId) {
        return sessionRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // End a session (set status to ENDED and record end time)
    public SessionResponseDTO endSession(Long id) {
        TelemedicineSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        session.setStatus("ENDED");
        session.setEndTime(LocalDateTime.now());
        TelemedicineSession updated = sessionRepository.save(session);
        return mapToResponseDTO(updated);
    }

    // Delete a session
    public void deleteSession(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new RuntimeException("Session not found with id: " + id);
        }
        sessionRepository.deleteById(id);
    }

    // Map Entity → ResponseDTO
    private SessionResponseDTO mapToResponseDTO(TelemedicineSession session) {
        return new SessionResponseDTO(
                session.getId(),
                session.getAppointmentId(),
                session.getDoctorId(),
                session.getPatientId(),
                session.getRoomName(),
                session.getSessionUrl(),
                session.getStatus(),
                session.getStartTime(),
                session.getEndTime()
        );
    }
}
