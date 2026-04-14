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

    private final AppointmentRepository appointmentRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${notification.service.url:http://localhost:8085}")
    private String notificationServiceUrl;

    @Value("${doctor.management.url:http://localhost:8082}")
    private String doctorManagementUrl;

    /** Create and save a new appointment with BOOKED status */
    @Transactional
    public AppointmentResponseDto bookAppointment(AppointmentRequestDto request) {
        Long resolvedDoctorId = resolveDoctorId(request.getDoctorId());

        // Skip all availability and duplicate checks for payment-based appointments
        // User has already paid, so we should allow the appointment creation
        // The availability was validated during the initial selection phase

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

        appointment = appointmentRepository.save(appointment);
        sendAppointmentNotification(appointment);
        return mapToResponseDto(appointment);
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
        appointment.setPhoneNumber(request.getPhoneNumber());

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

    public AvailabilityCheckResponseDto checkDoctorAvailability(Long doctorId, LocalDate date, String timeStr) {
        Long resolvedDoctorId = resolveDoctorId(doctorId);

        try {
            if (timeStr != null && !timeStr.isEmpty()) {
                java.time.LocalTime time = java.time.LocalTime.parse(timeStr);
                LocalDateTime selectedDateTime = date.atTime(time);
                validateDoctorAvailabilityForDate(resolvedDoctorId, selectedDateTime);
            } else {
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

    private Long resolveDoctorId(Long doctorId) {
        return doctorId != null ? doctorId : 1L;
    }

    private void validateDoctorAvailabilityForDate(Long doctorId, LocalDateTime appointmentDate) {
        // Check for conflicts within 1 hour window (30 minutes before and after)
        LocalDateTime timeWindowStart = appointmentDate.minusMinutes(30);
        LocalDateTime timeWindowEnd = appointmentDate.plusMinutes(30);

        boolean doctorHasAppointment = appointmentRepository.existsByDoctorIdAndAppointmentDateBetweenAndStatusIn(
                doctorId,
                timeWindowStart,
                timeWindowEnd,
                List.of(AppointmentStatus.BOOKED, AppointmentStatus.ACCEPTED)
        );

        if (doctorHasAppointment) {
            throw new ResponseStatusException(CONFLICT, "Doctor already has an appointment within 30 minutes of the selected time.");
        }

        if (isDoctorOnLeave(doctorId, appointmentDate.toLocalDate())) {
            throw new ResponseStatusException(CONFLICT, "Doctor is on leave on the selected day.");
        }
    }

    private boolean isDoctorOnLeave(Long doctorId, LocalDate selectedDate) {
        WebClient webClient = webClientBuilder.baseUrl(doctorManagementUrl).build();

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

    private String getDoctorName(Long doctorId) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(doctorManagementUrl).build();
            
            return webClient.get()
                    .uri("/api/doctors/{doctorId}/name", doctorId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException ex) {
            return null;
        }
    }
    private void sendAppointmentNotification(Appointment appointment) {
        try {
            log.info("Starting appointment notification process for appointment ID: {}", appointment.getId());
            
            // Use doctorName from appointment (no need to fetch since endpoint doesn't exist)
            String doctorName = appointment.getDoctorName() != null ? appointment.getDoctorName() : "Doctor";
            
            String notificationUrl = notificationServiceUrl + "/api/notifications/appointment/confirm";
            log.info("Calling notification service at: {}", notificationUrl);
            
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
            
            log.info("Notification payload: {}", payload);
            
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