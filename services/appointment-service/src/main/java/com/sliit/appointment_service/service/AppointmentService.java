package com.sliit.appointment_service.service;

import com.sliit.appointment_service.dto.AppointmentRequestDto;
import com.sliit.appointment_service.dto.AppointmentResponseDto;
import com.sliit.appointment_service.model.Appointment;
import com.sliit.appointment_service.model.AppointmentStatus;
import com.sliit.appointment_service.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    /** Create and save a new appointment with BOOKED status */
    @Transactional
    public AppointmentResponseDto bookAppointment(AppointmentRequestDto request) {
        Long resolvedDoctorId = resolveDoctorId(request.getDoctorId());
        boolean duplicateExists = appointmentRepository.existsByPatientIdAndDoctorIdAndAppointmentDateAndStatus(
            request.getPatientId(),
            resolvedDoctorId,
            request.getAppointmentDate(),
            AppointmentStatus.BOOKED
        );

        if (duplicateExists) {
            throw new ResponseStatusException(CONFLICT, "An appointment already exists for the selected date and time.");
        }

        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
            .doctorId(resolvedDoctorId)
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .appointmentDate(request.getAppointmentDate())
                .status(AppointmentStatus.BOOKED)
                .build();

        appointment = appointmentRepository.save(appointment);
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



    private AppointmentResponseDto mapToResponseDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
            .fullName(appointment.getFullName())
            .phoneNumber(appointment.getPhoneNumber())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .build();
    }

    private Long resolveDoctorId(Long doctorId) {
        return doctorId != null ? doctorId : 1L;
    }
}
