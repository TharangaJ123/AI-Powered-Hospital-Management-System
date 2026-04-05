package com.sliit.hospitalManagementSystem.doctor_management.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class AvailabilityScheduleDTO {

    private Long id;
    private Long doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDurationMinutes;
    private Integer maxPatientsPerSlot;
    private Boolean isAvailable;
    private String consultationType;

    public AvailabilityScheduleDTO() {}

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
    public String getConsultationType() { return consultationType; }
    public void setConsultationType(String consultationType) { this.consultationType = consultationType; }

    public static AvailabilityScheduleDTOBuilder builder() { return new AvailabilityScheduleDTOBuilder(); }

    public static class AvailabilityScheduleDTOBuilder {
        private Long id;
        private Long doctorId;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer slotDurationMinutes;
        private Integer maxPatientsPerSlot;
        private Boolean isAvailable;
        private String consultationType;

        public AvailabilityScheduleDTOBuilder id(Long id) { this.id = id; return this; }
        public AvailabilityScheduleDTOBuilder doctorId(Long doctorId) { this.doctorId = doctorId; return this; }
        public AvailabilityScheduleDTOBuilder dayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; return this; }
        public AvailabilityScheduleDTOBuilder startTime(LocalTime startTime) { this.startTime = startTime; return this; }
        public AvailabilityScheduleDTOBuilder endTime(LocalTime endTime) { this.endTime = endTime; return this; }
        public AvailabilityScheduleDTOBuilder slotDurationMinutes(Integer slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; return this; }
        public AvailabilityScheduleDTOBuilder maxPatientsPerSlot(Integer maxPatientsPerSlot) { this.maxPatientsPerSlot = maxPatientsPerSlot; return this; }
        public AvailabilityScheduleDTOBuilder isAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; return this; }
        public AvailabilityScheduleDTOBuilder consultationType(String consultationType) { this.consultationType = consultationType; return this; }

        public AvailabilityScheduleDTO build() {
            AvailabilityScheduleDTO d = new AvailabilityScheduleDTO();
            d.setId(this.id);
            d.setDoctorId(this.doctorId);
            d.setDayOfWeek(this.dayOfWeek);
            d.setStartTime(this.startTime);
            d.setEndTime(this.endTime);
            d.setSlotDurationMinutes(this.slotDurationMinutes);
            d.setMaxPatientsPerSlot(this.maxPatientsPerSlot);
            d.setIsAvailable(this.isAvailable);
            d.setConsultationType(this.consultationType);
            return d;
        }
    }
}
