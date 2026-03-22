package com.sliit.appointment_service.service;

import com.sliit.appointment_service.client.DoctorServiceClient;
import com.sliit.appointment_service.dto.AppointmentRequestDto;
import com.sliit.appointment_service.dto.AppointmentResponseDto;
import com.sliit.appointment_service.dto.DoctorDto;
import com.sliit.appointment_service.model.Appointment;
import com.sliit.appointment_service.model.AppointmentStatus;
import com.sliit.appointment_service.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorServiceClient doctorServiceClient;

    public List<DoctorDto> searchDoctorsBySpecialty(String specialty) {
        return doctorServiceClient.getDoctorsBySpecialty(specialty);
    }

    @Transactional
    public AppointmentResponseDto bookAppointment(AppointmentRequestDto request) {
        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .appointmentDate(request.getAppointmentDate())
                .status(AppointmentStatus.BOOKED)
                .build();

        appointment = appointmentRepository.save(appointment);
        return mapToResponseDto(appointment);
    }

    @Transactional
    public AppointmentResponseDto updateAppointment(Long id, AppointmentRequestDto request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(request.getDoctorId());

        appointment = appointmentRepository.save(appointment);
        return mapToResponseDto(appointment);
    }

    @Transactional
    public AppointmentResponseDto cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);
        return mapToResponseDto(appointment);
    }

    public AppointmentResponseDto getAppointmentStatus(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        return mapToResponseDto(appointment);
    }

    @Transactional
    public AppointmentResponseDto completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment = appointmentRepository.save(appointment);
        return mapToResponseDto(appointment);
    }

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
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .build();
    }
}
