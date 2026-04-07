package com.sliit.appointment_service.service;

import com.sliit.appointment_service.dto.AppointmentRequestDto;
import com.sliit.appointment_service.dto.AppointmentResponseDto;
import com.sliit.appointment_service.dto.AvailabilityCheckResponseDto;
import com.sliit.appointment_service.model.Appointment;
import com.sliit.appointment_service.model.AppointmentStatus;
import com.sliit.appointment_service.repository.AppointmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CONFLICT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final WebClient.Builder webClientBuilder;

    /** Create and save a new appointment with BOOKED status */
    @Transactional
    public AppointmentResponseDto bookAppointment(AppointmentRequestDto request) {
        log.info("=== BOOK APPOINTMENT: Received request for patientId={}, doctorId={} ===",
                request.getPatientId(), request.getDoctorId());

        Long resolvedDoctorId = resolveDoctorId(request.getDoctorId());

        validateDoctorAvailabilityForDate(resolvedDoctorId, request.getAppointmentDate());

        if (request.getPatientId() != null) {
            boolean duplicateExists = appointmentRepository.existsByPatientIdAndDoctorIdAndAppointmentDateAndStatus(
                request.getPatientId(),
                resolvedDoctorId,
                request.getAppointmentDate(),
                AppointmentStatus.BOOKED
            );

            if (duplicateExists) {
                throw new ResponseStatusException(CONFLICT, "An appointment already exists for the selected date and time.");
            }
        }

        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(resolvedDoctorId)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .appointmentDate(request.getAppointmentDate())
                .status(AppointmentStatus.BOOKED)
                .consultationType(request.getConsultationType())
                .reason(request.getReason())
                .doctorNotes(request.getDoctorNotes())
                .doctorName(request.getDoctorName())
                .specialty(request.getSpecialty())
                .build();

        appointment = appointmentRepository.save(appointment);
        log.info("=== BOOK APPOINTMENT: Saved appointment ID={}, now triggering notification ===", appointment.getId());
        
        // Trigger Email Notification
        try {
            sendAppointmentSummaryNotification(appointment);
        } catch (Exception e) {
            log.error("Failed to send notification for appointment {}: {}", appointment.getId(), e.getMessage());
        }

        return mapToResponseDto(appointment);
    }

    private void sendAppointmentSummaryNotification(Appointment appointment) {
        log.info("=== NOTIFICATION TRIGGER: Starting for appointment ID={} ===", appointment.getId());

        try {
            // Use data already available in the appointment - no blocking calls needed
            String recipientName = appointment.getFullName() != null ? appointment.getFullName() : "Patient";
            String recipientEmail = appointment.getEmail() != null && !appointment.getEmail().isEmpty() ? appointment.getEmail() : "patient@example.com"; 
            String doctorName = appointment.getDoctorName() != null ? appointment.getDoctorName() : "Specialist";
            String specialty = appointment.getSpecialty() != null ? appointment.getSpecialty() : "General";

            // Try to enrich with patient email if not provided in appointment (with timeout to prevent hanging)
            try {
                if ((appointment.getEmail() == null || appointment.getEmail().isEmpty()) && appointment.getPatientId() != null && appointment.getPatientId() != 0) {
                    Map profile = webClientBuilder.build()
                        .get()
                        .uri("http://user-management/api/patients/" + appointment.getPatientId() + "/profile")
                        .retrieve()
                        .bodyToMono(Map.class)
                        .timeout(java.time.Duration.ofSeconds(5))
                        .onErrorReturn(java.util.Collections.emptyMap())
                        .block();

                    if (profile != null && profile.containsKey("email") && profile.get("email") != null) {
                        recipientEmail = (String) profile.get("email");
                    }
                }
            } catch (Exception e) {
                log.warn("Could not fetch patient email, using fallback: {}", e.getMessage());
            }

            // Try to enrich with doctor details (with timeout)
            try {
                if (appointment.getDoctorId() != null && appointment.getDoctorId() != 0) {
                    Map doctorProfile = webClientBuilder.build()
                        .get()
                        .uri("http://doctor-management/api/doctors/profiles/" + appointment.getDoctorId())
                        .retrieve()
                        .bodyToMono(Map.class)
                        .timeout(java.time.Duration.ofSeconds(5))
                        .onErrorReturn(java.util.Collections.emptyMap())
                        .block();

                    if (doctorProfile != null) {
                        if (doctorProfile.containsKey("firstName")) {
                            doctorName = doctorProfile.get("firstName") + " " + doctorProfile.getOrDefault("lastName", "");
                        }
                        if (doctorProfile.containsKey("specialization")) {
                            specialty = (String) doctorProfile.get("specialization");
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Could not fetch doctor details, using fallback: {}", e.getMessage());
            }

            // Build the notification payload — ensure no null values for required fields
            Map<String, Object> notification = new java.util.HashMap<>();
            notification.put("type", "APPOINTMENT_BOOKED");
            notification.put("recipientName", recipientName);
            notification.put("recipientEmail", recipientEmail);
            notification.put("recipientPhone", appointment.getPhoneNumber() != null ? appointment.getPhoneNumber() : "");
            notification.put("appointmentId", "APT-" + appointment.getId());
            notification.put("patientName", recipientName);
            notification.put("doctorName", doctorName);
            notification.put("specialty", specialty);
            notification.put("appointmentDate", appointment.getAppointmentDate().toLocalDate().toString());
            notification.put("appointmentTime", appointment.getAppointmentDate().toLocalTime().toString());
            notification.put("consultationType", appointment.getConsultationType() != null ? appointment.getConsultationType() : "General Booking");
            notification.put("reason", appointment.getReason() != null ? appointment.getReason() : "Direct Booking");
            notification.put("doctorNotes", appointment.getDoctorNotes() != null ? appointment.getDoctorNotes() : "None");

            log.info("=== NOTIFICATION CALL: Sending to notification-service for appointment {} | email={} ===",
                    appointment.getId(), recipientEmail);

            webClientBuilder.build()
                .post()
                .uri("http://notification-service/api/notifications/send")
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(java.time.Duration.ofSeconds(10))
                .doOnSuccess(response -> log.info("=== NOTIFICATION SUCCESS for appointment {}: {} ===", appointment.getId(), response))
                .doOnError(error -> log.error("=== NOTIFICATION FAILED for appointment {}: {} ===", appointment.getId(), error.getMessage()))
                .subscribe();

        } catch (Exception e) {
            log.error("=== NOTIFICATION ERROR: Failed to build/send notification: {} ===", e.getMessage(), e);
        }
    }

    /** Update an existing appointment's details */
    @Transactional
    public AppointmentResponseDto updateAppointment(Long id, AppointmentRequestDto request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(resolveDoctorId(request.getDoctorId()));
        appointment.setFullName(request.getFullName());
        appointment.setEmail(request.getEmail());
        appointment.setPhoneNumber(request.getPhoneNumber());
        appointment.setDoctorName(request.getDoctorName());
        appointment.setSpecialty(request.getSpecialty());

        appointment = appointmentRepository.save(appointment);
        return mapToResponseDto(appointment);
    }

    /** Soft delete an appointment by setting its status to CANCELLED */
    @Transactional
    public AppointmentResponseDto cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);
        return mapToResponseDto(appointment);
    }

    /** Fetch the status and details of a single appointment */
    public AppointmentResponseDto getAppointmentStatus(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        return mapToResponseDto(appointment);
    }

    /** Update the status of an appointment to COMPLETED */
    @Transactional
    public AppointmentResponseDto completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment = appointmentRepository.save(appointment);
        return mapToResponseDto(appointment);
    }

    /** Fetch all appointments in the system */
    public List<AppointmentResponseDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /** Hard delete an appointment from the database */
    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    /** Fetch all appointments belonging to a specific patient */
    public List<AppointmentResponseDto> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /** Fetch all appointments scheduled for a specific doctor */
    public List<AppointmentResponseDto> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public AvailabilityCheckResponseDto checkDoctorAvailability(Long doctorId, LocalDate date) {
        Long resolvedDoctorId = resolveDoctorId(doctorId);
        LocalDateTime selectedDateTime = date.atStartOfDay();

        try {
            validateDoctorAvailabilityForDate(resolvedDoctorId, selectedDateTime);
            return AvailabilityCheckResponseDto.builder()
                    .available(true)
                    .message("Doctor is available for the selected day.")
                    .build();
        } catch (ResponseStatusException ex) {
            return AvailabilityCheckResponseDto.builder()
                    .available(false)
                    .message(ex.getReason())
                    .build();
        }
    }



    private AppointmentResponseDto mapToResponseDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .fullName(appointment.getFullName())
                .email(appointment.getEmail())
                .phoneNumber(appointment.getPhoneNumber())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .doctorName(appointment.getDoctorName())
                .specialty(appointment.getSpecialty())
                .build();
    }

    private Long resolveDoctorId(Long doctorId) {
        return doctorId != null ? doctorId : 1L;
    }

    private void validateDoctorAvailabilityForDate(Long doctorId, LocalDateTime appointmentDate) {
        LocalDate selectedDate = appointmentDate.toLocalDate();
        LocalDateTime dayStart = selectedDate.atStartOfDay();
        LocalDateTime dayEnd = selectedDate.plusDays(1).atStartOfDay().minusNanos(1);

        boolean doctorHasAppointment = appointmentRepository.existsByDoctorIdAndAppointmentDateBetweenAndStatusIn(
                doctorId,
                dayStart,
                dayEnd,
                List.of(AppointmentStatus.BOOKED, AppointmentStatus.ACCEPTED)
        );

        if (doctorHasAppointment) {
            throw new ResponseStatusException(CONFLICT, "Doctor already has an appointment on the selected day.");
        }

        if (isDoctorOnLeave(doctorId, selectedDate)) {
            throw new ResponseStatusException(CONFLICT, "Doctor is on leave on the selected day.");
        }
    }

    private boolean isDoctorOnLeave(Long doctorId, LocalDate selectedDate) {
        WebClient webClient = webClientBuilder.baseUrl("http://localhost:8085").build();

        try {
            List<DoctorLeaveView> leaves = webClient.get()
                    .uri("/api/doctors/leaves/doctor/{doctorId}", doctorId)
                    .retrieve()
                    .bodyToFlux(DoctorLeaveView.class)
                    .collectList()
                    .block();

            if (leaves == null) {
                return false;
            }

            return leaves.stream()
                    .filter(Objects::nonNull)
                    .filter(leave -> !"REJECTED".equalsIgnoreCase(leave.getStatus()))
                    .anyMatch(leave -> !selectedDate.isBefore(leave.getStartDate()) && !selectedDate.isAfter(leave.getEndDate()));
        } catch (WebClientResponseException ex) {
            return false;
        }
    }

    private static class DoctorLeaveView {
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;

        public LocalDate getStartDate() {
            return startDate;
        }

        @SuppressWarnings("unused")
        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        @SuppressWarnings("unused")
        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public String getStatus() {
            return status;
        }

        @SuppressWarnings("unused")
        public void setStatus(String status) {
            this.status = status;
        }
    }
}
