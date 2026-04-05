package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.AppointmentRequestDTO;
import com.sliit.hospitalManagementSystem.doctor_management.model.AppointmentRequest;
import com.sliit.hospitalManagementSystem.doctor_management.model.AppointmentRequestStatus;
import com.sliit.hospitalManagementSystem.doctor_management.model.ConsultationType;
import com.sliit.hospitalManagementSystem.doctor_management.repository.AppointmentRequestRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentRequestService {

    private final AppointmentRequestRepository appointmentRequestRepository;

    public AppointmentRequestService(AppointmentRequestRepository appointmentRequestRepository) {
        this.appointmentRequestRepository = appointmentRequestRepository;
    }

    @SuppressWarnings("null")
    public AppointmentRequestDTO createRequest(AppointmentRequestDTO dto) {
        AppointmentRequest request = mapToEntity(dto);
        return mapToDTO(appointmentRequestRepository.save(request));
    }

    public AppointmentRequestDTO getRequestById(@NonNull Long id) {
        return appointmentRequestRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Appointment request not found with id: " + id));
    }

    public List<AppointmentRequestDTO> getRequestsByDoctorId(Long doctorId) {
        return appointmentRequestRepository.findByDoctorIdOrderByRequestedDateTimeDesc(doctorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentRequestDTO> getPendingRequestsByDoctorId(Long doctorId) {
        return appointmentRequestRepository.findByDoctorIdAndStatus(doctorId, AppointmentRequestStatus.PENDING).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentRequestDTO acceptRequest(@NonNull Long id, String doctorNotes) {
        return appointmentRequestRepository.findById(id)
                .map(request -> {
                    request.setStatus(AppointmentRequestStatus.ACCEPTED);
                    request.setDoctorNotes(doctorNotes);
                    return mapToDTO(appointmentRequestRepository.save(request));
                })
                .orElseThrow(() -> new RuntimeException("Appointment request not found with id: " + id));
    }

    public AppointmentRequestDTO rejectRequest(@NonNull Long id, String doctorNotes) {
        return appointmentRequestRepository.findById(id)
                .map(request -> {
                    request.setStatus(AppointmentRequestStatus.REJECTED);
                    request.setDoctorNotes(doctorNotes);
                    return mapToDTO(appointmentRequestRepository.save(request));
                })
                .orElseThrow(() -> new RuntimeException("Appointment request not found with id: " + id));
    }

    public AppointmentRequestDTO completeRequest(@NonNull Long id) {
        return appointmentRequestRepository.findById(id)
                .map(request -> {
                    request.setStatus(AppointmentRequestStatus.COMPLETED);
                    return mapToDTO(appointmentRequestRepository.save(request));
                })
                .orElseThrow(() -> new RuntimeException("Appointment request not found with id: " + id));
    }

    // --- Mapping helpers ---

    private AppointmentRequestDTO mapToDTO(@NonNull AppointmentRequest request) {
        return AppointmentRequestDTO.builder()
                .id(request.getId())
                .doctorId(request.getDoctorId())
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .requestedDateTime(request.getRequestedDateTime())
                .consultationType(request.getConsultationType() != null ? request.getConsultationType().name() : null)
                .reason(request.getReason())
                .status(request.getStatus() != null ? request.getStatus().name() : null)
                .doctorNotes(request.getDoctorNotes())
                .build();
    }

    private AppointmentRequest mapToEntity(AppointmentRequestDTO dto) {
        return AppointmentRequest.builder()
                .doctorId(dto.getDoctorId())
                .patientId(dto.getPatientId())
                .patientName(dto.getPatientName())
                .requestedDateTime(dto.getRequestedDateTime())
                .consultationType(dto.getConsultationType() != null ?
                        ConsultationType.valueOf(dto.getConsultationType().toUpperCase()) : null)
                .reason(dto.getReason())
                .status(AppointmentRequestStatus.PENDING)
                .build();
    }
}
