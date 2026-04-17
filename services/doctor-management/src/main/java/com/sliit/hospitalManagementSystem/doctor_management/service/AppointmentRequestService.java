package com.sliit.hospitalManagementSystem.doctor_management.service;

import com.sliit.hospitalManagementSystem.doctor_management.dto.AppointmentRequestDTO;
import com.sliit.hospitalManagementSystem.doctor_management.dto.NotificationRequest;
import com.sliit.hospitalManagementSystem.doctor_management.model.AppointmentRequest;
import com.sliit.hospitalManagementSystem.doctor_management.model.AppointmentRequestStatus;
import com.sliit.hospitalManagementSystem.doctor_management.model.ConsultationType;
import com.sliit.hospitalManagementSystem.doctor_management.model.DoctorProfile;
import com.sliit.hospitalManagementSystem.doctor_management.repository.AppointmentRequestRepository;
import com.sliit.hospitalManagementSystem.doctor_management.repository.DoctorProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppointmentRequestService {

    private final AppointmentRequestRepository appointmentRequestRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final WebClient.Builder webClientBuilder;

    public AppointmentRequestService(AppointmentRequestRepository appointmentRequestRepository,
                                   DoctorProfileRepository doctorProfileRepository,
                                   WebClient.Builder webClientBuilder) {
        this.appointmentRequestRepository = appointmentRequestRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.webClientBuilder = webClientBuilder;
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
                    AppointmentRequest saved = appointmentRequestRepository.save(request);
                    
                    // Trigger Notification
                    try {
                        sendAppointmentSummaryNotification(saved);
                    } catch (Exception e) {
                        log.error("Failed to send notification for appointment {}: {}", id, e.getMessage());
                    }
                    
                    return mapToDTO(saved);
                })
                .orElseThrow(() -> new RuntimeException("Appointment request not found with id: " + id));
    }

    private void sendAppointmentSummaryNotification(AppointmentRequest request) {
        // 1. Fetch Patient Email and Phone from user-management
        // We use .block() here for simplicity as the return type of acceptRequest is not reactive
        // In a real production app, consider using non-blocking calls or async execution
        try {
            // Get User Response (for email)
            Mono<Object> userMono = webClientBuilder.build()
                .get()
                .uri("http://user-management/api/patients/" + request.getPatientId() + "/profile")
                .retrieve()
                .bodyToMono(Object.class);
            
            // For now, we'll try to get the profile and any relevant info.
            // Since UserResponseDto and PatientProfileDto are in another service, 
            // we'll use a Map to read the response dynamically to avoid DTO duplication issues.
            java.util.Map profile = webClientBuilder.build()
                .get()
                .uri("http://user-management/api/patients/" + request.getPatientId() + "/profile")
                .retrieve()
                .bodyToMono(java.util.Map.class)
                .block();

            // Also check doctor details
            DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId()).orElse(null);
            
            if (profile != null) {
                NotificationRequest notification = NotificationRequest.builder()
                    .type(NotificationRequest.NotificationType.APPOINTMENT_BOOKED)
                    .recipientName(request.getPatientName())
                    .recipientEmail((String) profile.get("email")) // This might need a separate call to get User email
                    .recipientPhone((String) profile.get("phoneNumber"))
                    .appointmentId("APT-" + request.getId())
                    .patientName(request.getPatientName())
                    .doctorName(doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : "Specialist")
                    .specialty(doctor != null ? doctor.getSpecialization() : "General")
                    .appointmentDate(request.getRequestedDateTime().toLocalDate().toString())
                    .appointmentTime(request.getRequestedDateTime().toLocalTime().toString())
                    .consultationType(request.getConsultationType() != null ? request.getConsultationType().name() : "In-Person")
                    .reason(request.getReason())
                    .doctorNotes(request.getDoctorNotes())
                    .build();

                // If email is missing from profile, try fetching from user endpoint
                if (notification.getRecipientEmail() == null) {
                    java.util.Map userEntry = webClientBuilder.build()
                        .get()
                        .uri("http://user-management/api/auth/user/" + request.getPatientId()) // Assuming this exists or similar
                        .retrieve()
                        .bodyToMono(java.util.Map.class)
                        .onErrorReturn(java.util.Collections.emptyMap())
                        .block();
                    if (userEntry != null) {
                        notification.setRecipientEmail((String) userEntry.get("email"));
                    }
                }

                // Fallback email if still null for testing
                if (notification.getRecipientEmail() == null) {
                    notification.setRecipientEmail("patient@example.com");
                }

                log.info("Sending full summary email to {}", notification.getRecipientEmail());

                webClientBuilder.build()
                    .post()
                    .uri("http://notification-service/api/notifications/send")
                    .bodyValue(notification)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .subscribe(); // Async call to notification service
            }
        } catch (Exception e) {
            log.error("Error building or sending notification: {}", e.getMessage());
        }
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
