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

    // Handles the creation of a new appointment request from a DTO
    @SuppressWarnings("null")
    public AppointmentRequestDTO createRequest(AppointmentRequestDTO dto) {
        AppointmentRequest request = mapToEntity(dto);
        return mapToDTO(appointmentRequestRepository.save(request));
    }

    // Retrieves a specific appointment request by its unique identifier
    public AppointmentRequestDTO getRequestById(@NonNull Long id) {
        return appointmentRequestRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Appointment request not found with id: " + id));
    }

    // Fetches all requests for a doctor, ordered by the requested date and time
    public List<AppointmentRequestDTO> getRequestsByDoctorId(Long doctorId) {
        return appointmentRequestRepository.findByDoctorIdOrderByRequestedDateTimeDesc(doctorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Retrieves only the requests that are currently in 'PENDING' status for a doctor
    public List<AppointmentRequestDTO> getPendingRequestsByDoctorId(Long doctorId) {
        return appointmentRequestRepository.findByDoctorIdAndStatus(doctorId, AppointmentRequestStatus.PENDING).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Marks an appointment as 'ACCEPTED' and triggers a summary notification to the patient
    public AppointmentRequestDTO acceptRequest(@NonNull Long id, String doctorNotes) {
        return appointmentRequestRepository.findById(id)
                .map(request -> {
                    request.setStatus(AppointmentRequestStatus.ACCEPTED);
                    request.setDoctorNotes(doctorNotes);
                    AppointmentRequest saved = appointmentRequestRepository.save(request);
                    
                    // Trigger asynchronous notification to notify the patient about the acceptance
                    try {
                        sendAppointmentSummaryNotification(saved);
                    } catch (Exception e) {
                        log.error("Failed to send notification for appointment {}: {}", id, e.getMessage());
                    }
                    
                    return mapToDTO(saved);
                })
                .orElseThrow(() -> new RuntimeException("Appointment request not found with id: " + id));
    }

    // Internal method to orchestrate cross-service calls to fetch patient data and send notifications
    private void sendAppointmentSummaryNotification(AppointmentRequest request) {
        try {
            // Fetch patient profile details from user-management microservice using WebClient
            java.util.Map profile = webClientBuilder.build()
                .get()
                .uri("http://user-management/api/patients/" + request.getPatientId() + "/profile")
                .retrieve()
                .bodyToMono(java.util.Map.class)
                .block();

            // Fetch current doctor profile to include name and specialization in the notification
            DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId()).orElse(null);
            
            if (profile != null) {
                // Construct a detailed notification request object
                NotificationRequest notification = NotificationRequest.builder()
                    .type(NotificationRequest.NotificationType.APPOINTMENT_BOOKED)
                    .recipientName(request.getPatientName())
                    .recipientEmail((String) profile.get("email")) 
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

                // Fallback mechanism to fetch email from auth endpoint if not found in profile
                if (notification.getRecipientEmail() == null) {
                    java.util.Map userEntry = webClientBuilder.build()
                        .get()
                        .uri("http://user-management/api/auth/user/" + request.getPatientId()) 
                        .retrieve()
                        .bodyToMono(java.util.Map.class)
                        .onErrorReturn(java.util.Collections.emptyMap())
                        .block();
                    if (userEntry != null) {
                        notification.setRecipientEmail((String) userEntry.get("email"));
                    }
                }

                // Default fallback for testing environments
                if (notification.getRecipientEmail() == null) {
                    notification.setRecipientEmail("patient@example.com");
                }

                log.info("Sending full summary email to {}", notification.getRecipientEmail());

                // Post the notification request to the notification-service
                webClientBuilder.build()
                    .post()
                    .uri("http://notification-service/api/notifications/send")
                    .bodyValue(notification)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .subscribe(); 
            }
        } catch (Exception e) {
            log.error("Error building or sending notification: {}", e.getMessage());
        }
    }

    // Marks an appointment request as 'REJECTED'
    public AppointmentRequestDTO rejectRequest(@NonNull Long id, String doctorNotes) {
        return appointmentRequestRepository.findById(id)
                .map(request -> {
                    request.setStatus(AppointmentRequestStatus.REJECTED);
                    request.setDoctorNotes(doctorNotes);
                    return mapToDTO(appointmentRequestRepository.save(request));
                })
                .orElseThrow(() -> new RuntimeException("Appointment request not found with id: " + id));
    }

    // Updates the status to 'COMPLETED' after a successful medical consultation
    public AppointmentRequestDTO completeRequest(@NonNull Long id) {
        return appointmentRequestRepository.findById(id)
                .map(request -> {
                    request.setStatus(AppointmentRequestStatus.COMPLETED);
                    return mapToDTO(appointmentRequestRepository.save(request));
                })
                .orElseThrow(() -> new RuntimeException("Appointment request not found with id: " + id));
    }

    // Utility to convert an AppointmentRequest JPA entity to a data transfer object
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

    // Utility to convert a DTO into an AppointmentRequest entity for database persistence
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
