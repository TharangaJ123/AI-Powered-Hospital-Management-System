package com.sliit.hospitalManagementSystem.doctor_management.model;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "availability_schedules")
public class AvailabilitySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "slot_duration_minutes")
    private Integer slotDurationMinutes;

    @Column(name = "max_patients_per_slot")
    private Integer maxPatientsPerSlot;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Enumerated(EnumType.STRING)
    @Column(name = "consultation_type")
    private ConsultationType consultationType;

    public AvailabilitySchedule() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public Integer getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(Integer slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }
    public Integer getMaxPatientsPerSlot() { return maxPatientsPerSlot; }
    public void setMaxPatientsPerSlot(Integer maxPatientsPerSlot) { this.maxPatientsPerSlot = maxPatientsPerSlot; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    public ConsultationType getConsultationType() { return consultationType; }
    public void setConsultationType(ConsultationType consultationType) { this.consultationType = consultationType; }

    public static AvailabilityScheduleBuilder builder() { return new AvailabilityScheduleBuilder(); }

    public static class AvailabilityScheduleBuilder {
        private Long doctorId;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer slotDurationMinutes;
        private Integer maxPatientsPerSlot;
        private Boolean isAvailable;
        private ConsultationType consultationType;

        public AvailabilityScheduleBuilder doctorId(Long doctorId) { this.doctorId = doctorId; return this; }
        public AvailabilityScheduleBuilder dayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; return this; }
        public AvailabilityScheduleBuilder startTime(LocalTime startTime) { this.startTime = startTime; return this; }
        public AvailabilityScheduleBuilder endTime(LocalTime endTime) { this.endTime = endTime; return this; }
        public AvailabilityScheduleBuilder slotDurationMinutes(Integer slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; return this; }
        public AvailabilityScheduleBuilder maxPatientsPerSlot(Integer maxPatientsPerSlot) { this.maxPatientsPerSlot = maxPatientsPerSlot; return this; }
        public AvailabilityScheduleBuilder isAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; return this; }
        public AvailabilityScheduleBuilder consultationType(ConsultationType consultationType) { this.consultationType = consultationType; return this; }

        public AvailabilitySchedule build() {
            AvailabilitySchedule s = new AvailabilitySchedule();
            s.setDoctorId(this.doctorId);
            s.setDayOfWeek(this.dayOfWeek);
            s.setStartTime(this.startTime);
            s.setEndTime(this.endTime);
            s.setSlotDurationMinutes(this.slotDurationMinutes);
            s.setMaxPatientsPerSlot(this.maxPatientsPerSlot);
            s.setIsAvailable(this.isAvailable);
            s.setConsultationType(this.consultationType);
            return s;
        }
    }
}
