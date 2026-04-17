package com.sliit.appointment_service.service;

import com.sliit.appointment_service.dto.AppointmentRequestDto;
import com.sliit.appointment_service.dto.AppointmentResponseDto;
import com.sliit.appointment_service.dto.AvailabilityCheckResponseDto;
import com.sliit.appointment_service.model.Appointment;
import com.sliit.appointment_service.model.AppointmentStatus;
import com.sliit.appointment_service.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    // Repository for persistence operations on appointments
    private final AppointmentRepository appointmentRepository;
    // WebClient builder for making requests to other microservices
    private final WebClient.Builder webClientBuilder;

    // URL for the Notification service, injected from properties
    @Value("${notification.service.url:http://localhost:8085}")
    private String notificationServiceUrl;

    // URL for the Doctor Management service, injected from properties
    @Value("${doctor.management.url:http://localhost:8082}")
    private String doctorManagementUrl;

    // Orchestrates the booking of a new appointment and persists it to the database
    @Transactional
    public AppointmentResponseDto bookAppointment(AppointmentRequestDto request) {
        Long resolvedDoctorId = resolveDoctorId(request.getDoctorId());

        // Construct the appointment entity from the request data
        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(resolvedDoctorId)
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .appointmentDate(request.getAppointmentDate())
                .consultationType(request.getConsultationType() != null ? request.getConsultationType() : "")
                .reason(request.getReason() != null ? request.getReason() : "")
                .doctorName(request.getDoctorName())
                .specialty(request.getSpecialty() != null ? request.getSpecialty() : "")
                .status(AppointmentStatus.BOOKED)
                .build();

        // Save to database and trigger a confirmation notification
        appointment = appointmentRepository.save(appointment);
        sendAppointmentNotification(appointment);
        return mapToResponseDto(appointment);
    }

    // Updates an existing appointment's details based on the provided request
    @Transactional
    public AppointmentResponseDto updateAppointment(Long id, AppointmentRequestDto request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        // Update fields with new values from the request
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(resolveDoctorId(request.getDoctorId()));
        appointment.setFullName(request.getFullName());
        appointment.setPhoneNumber(request.getPhoneNumber());

        appointment = appointmentRepository.save(appointment);
        return mapToResponseDto(appointment);
    }

    // Soft-deletes an appointment by transitioning its status to CANCELLED
    @Transactional
    public AppointmentResponseDto cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);
        return mapToResponseDto(appointment);
    }

    // Retrieves current details for a specific appointment ID
    public AppointmentResponseDto getAppointmentStatus(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        return mapToResponseDto(appointment);
    }

    // Finalizes an appointment by marking it as COMPLETED
    @Transactional
    public AppointmentResponseDto completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment = appointmentRepository.save(appointment);
        return mapToResponseDto(appointment);
    }

    // Retrieves a list of all appointment records across the system
    public List<AppointmentResponseDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Permanently removes an appointment record from the persistent store
    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    // Returns a list of appointments associated with a specific patient
    public List<AppointmentResponseDto> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Returns a list of appointments assigned to a specific doctor
    public List<AppointmentResponseDto> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Verifies if a doctor is available on a specific date and optional time slot
    public AvailabilityCheckResponseDto checkDoctorAvailability(Long doctorId, LocalDate date, String timeStr) {
        Long resolvedDoctorId = resolveDoctorId(doctorId);

        try {
            if (timeStr != null && !timeStr.isEmpty()) {
                // Perform granular time-slot validation
                java.time.LocalTime time = java.time.LocalTime.parse(timeStr);
                LocalDateTime selectedDateTime = date.atTime(time);
                validateDoctorAvailabilityForDate(resolvedDoctorId, selectedDateTime);
            } else {
                // Perform high-level day-off validation
                if (isDoctorOnLeave(resolvedDoctorId, date)) {
                    throw new ResponseStatusException(CONFLICT, "Doctor is on leave on the selected day.");
                }
            }
            return AvailabilityCheckResponseDto.builder()
                    .available(true)
                    .message("Doctor is available.")
                    .build();
        } catch (ResponseStatusException ex) {
            return AvailabilityCheckResponseDto.builder()
                    .available(false)
                    .message(ex.getReason())
                    .build();
        }
    }

    // Maps the internal Appointment entity to a public Response DTO
    private AppointmentResponseDto mapToResponseDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .fullName(appointment.getFullName())
                .phoneNumber(appointment.getPhoneNumber())
                .email(appointment.getEmail())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .doctorName(appointment.getDoctorName())
                .specialty(appointment.getSpecialty())
                .build();
    }

    // Ensures a valid doctor ID is used, defaulting to 1L if null
    private Long resolveDoctorId(Long doctorId) {
        return doctorId != null ? doctorId : 1L;
    }

    // Checks for overlapping appointments and leave schedules for a specific time slot
    private void validateDoctorAvailabilityForDate(Long doctorId, LocalDateTime appointmentDate) {
        LocalDateTime timeWindowStart = appointmentDate.minusMinutes(30);
        LocalDateTime timeWindowEnd = appointmentDate.plusMinutes(30);

        // Verify if any other scheduled or accepted appointments conflict with this window
        boolean doctorHasAppointment = appointmentRepository.existsByDoctorIdAndAppointmentDateBetweenAndStatusIn(
                doctorId,
                timeWindowStart,
                timeWindowEnd,
                List.of(AppointmentStatus.BOOKED, AppointmentStatus.ACCEPTED)
        );

        if (doctorHasAppointment) {
            throw new ResponseStatusException(CONFLICT, "Doctor already has an appointment within 30 minutes of the selected time.");
        }

        // Cross-reference with the doctor's approved leave dates
        if (isDoctorOnLeave(doctorId, appointmentDate.toLocalDate())) {
            throw new ResponseStatusException(CONFLICT, "Doctor is on leave on the selected day.");
        }
    }

    // Queries the Doctor Management service to check if the doctor is on leave
    private boolean isDoctorOnLeave(Long doctorId, LocalDate selectedDate) {
        WebClient webClient = webClientBuilder.baseUrl(doctorManagementUrl).build();

        try {
            // Fetch leave records from the external doctor management microservice
            List<DoctorLeaveView> leaves = webClient.get()
                    .uri("/api/doctors/leaves/doctor/{doctorId}", doctorId)
                    .retrieve()
                    .bodyToFlux(DoctorLeaveView.class)
                    .collectList()
                    .block();

            if (leaves == null) {
                return false;
            }

            // Check if the selected date falls within any non-rejected leave period
            return leaves.stream()
                    .filter(Objects::nonNull)
                    .filter(leave -> !"REJECTED".equalsIgnoreCase(leave.getStatus()))
                    .anyMatch(leave -> !selectedDate.isBefore(leave.getStartDate()) && !selectedDate.isAfter(leave.getEndDate()));
        } catch (WebClientResponseException ex) {
            return false;
        }
    }

    // Internal view class for parsing doctor leave data from JSON
    private static class DoctorLeaveView {
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;

        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public String getStatus() { return status; }
    }

    // Triggers a confirmation email/SMS via the Notification microservice
    private void sendAppointmentNotification(Appointment appointment) {
        try {
            log.info("Starting appointment notification process for appointment ID: {}", appointment.getId());
            
            String doctorName = appointment.getDoctorName() != null ? appointment.getDoctorName() : "Doctor";
            String notificationUrl = notificationServiceUrl + "/api/notifications/appointment/confirm";
            
            // Build the multi-channel notification payload
            java.util.Map<String, Object> payload = java.util.Map.of(
                "appointmentId", appointment.getId().toString(),
                "email", appointment.getEmail(),
                "customerName", appointment.getFullName(),
                "phoneNumber", appointment.getPhoneNumber(),
                "doctorName", doctorName,
                "appointmentDate", appointment.getAppointmentDate().toString(),
                "specialty", appointment.getSpecialty() != null && !appointment.getSpecialty().isEmpty() ? appointment.getSpecialty() : "General",
                "consultationType", appointment.getConsultationType() != null && !appointment.getConsultationType().isEmpty() ? appointment.getConsultationType() : "In-Person",
                "reason", appointment.getReason() != null && !appointment.getReason().isEmpty() ? appointment.getReason() : "Regular checkup"
            );
            
            // Post notification data to the notification service
            String response = webClientBuilder.build()
                .post()
                .uri(notificationUrl)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
                
            log.info("Appointment confirmation sent to {}: {}", appointment.getEmail(), response);
                
        } catch (Exception e) {
            log.error("Error sending appointment notification: {}", e.getMessage(), e);
        }
    }
}