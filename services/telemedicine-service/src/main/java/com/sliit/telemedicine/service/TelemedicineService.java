package com.sliit.telemedicine.service;

import com.sliit.telemedicine.dto.TelemedicineSessionResponse;
import com.sliit.telemedicine.model.TelemedicineSession;
import com.sliit.telemedicine.repository.TelemedicineSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class TelemedicineService {

    private final TelemedicineSessionRepository sessionRepository;
    private static final String JITSI_BASE_URL = "https://meet.jit.si/";

    @SuppressWarnings("null")
    public TelemedicineSession createOrGetSession(@NonNull Long appointmentId) {
        System.out.println("DEBUG: Creating/Getting session for appointment: " + appointmentId);
        return sessionRepository.findByAppointmentId(appointmentId)
                .orElseGet(() -> {
                    System.out.println("DEBUG: No session found, creating new one...");
                    String roomName = "hospital-mgmt-" + appointmentId + "-" + UUID.randomUUID().toString().substring(0, 8);
                    TelemedicineSession session = TelemedicineSession.builder()
                            .appointmentId(appointmentId)
                            .roomName(roomName)
                            .status(TelemedicineSession.SessionStatus.CREATED)
                            .build();
                    TelemedicineSession saved = sessionRepository.save(session);
                    System.out.println("DEBUG: Successfully saved session ID: " + saved.getId());
                    return saved;
                });
    }

    public TelemedicineSessionResponse getJoinSessionDetails(@NonNull Long appointmentId) {
        TelemedicineSession session = sessionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Session not found for appointment " + appointmentId));

        return TelemedicineSessionResponse.builder()
                .roomName(session.getRoomName())
                .jitsiUrl(JITSI_BASE_URL + session.getRoomName())
                .build();
    }

    public TelemedicineSession completeSession(@NonNull Long appointmentId) {
        return sessionRepository.findByAppointmentId(appointmentId)
                .map(session -> {
                    session.setStatus(TelemedicineSession.SessionStatus.COMPLETED);
                    return sessionRepository.save(session);
                })
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }
}