package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.AppointmentConverter;
import org.project.entity.AppointmentEntity;
import org.project.entity.StaffScheduleEntity;
import org.project.enums.AppointmentStatus;
import org.project.model.dto.AppointmentDTO;
import org.project.repository.*;
import org.project.service.AppointmentService;
import org.project.utils.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private ServiceRepository serviceRepository;
    private StaffScheduleRepository staffScheduleRepository;
    private PatientRepository patientRepository;
    private AppointmentConverter appointmentConverter;
    private TimestampUtils timestampUtils;

    @Autowired
    public void setAppointmentRepository(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Autowired
    public void setDoctorRepository(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Autowired
    public void setServiceRepository(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Autowired
    public void setStaffScheduleRepository(StaffScheduleRepository staffScheduleRepository) {
        this.staffScheduleRepository = staffScheduleRepository;
    }

    @Autowired
    public void setPatientRepository(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Autowired
    public void setAppointmentConverter(AppointmentConverter appointmentConverter) {
        this.appointmentConverter = appointmentConverter;
    }

    private boolean isDoctorExists(AppointmentDTO appointmentDTO) {
        Long doctorId = appointmentDTO.getDoctorEntityId();
        if (doctorId == null) {
            return false;
        }
        return doctorRepository.existsById(doctorId);
    }

    private boolean isDoctorSupportService(AppointmentDTO appointmentDTO) {
        Long doctorId = appointmentDTO.getDoctorEntityId();
        Long serviceId = appointmentDTO.getServiceEntityId();
        if (serviceId == null) {
            return false;
        }
        return serviceRepository.existsByIdAndDepartmentEntityStaffEntitiesId(serviceId, doctorId);
    }

    private boolean isStartTimeValid(AppointmentDTO appointmentDTO) {
        Timestamp startTime = appointmentDTO.getStartTime();
        if (startTime == null) {
            return false;
        }
        return startTime.after(new Timestamp(System.currentTimeMillis()));
    }

    private boolean isStartTimeBetweenStaffSchedule(AppointmentDTO appointmentDTO) {
        Long doctorId = appointmentDTO.getDoctorEntityId();
        Timestamp startTime = appointmentDTO.getStartTime();
        List<StaffScheduleEntity> staffScheduleEntity = staffScheduleRepository.findByStaffEntityIdAndAvailableDate(doctorId, new Date(startTime.getTime()));
        return staffScheduleEntity.stream().anyMatch(schedule -> {
            if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
                return false; // Invalid schedule
            }
            Timestamp scheduleStartTime = schedule.getStartTime();
            Timestamp scheduleEndTime = schedule.getEndTime();
            scheduleEndTime = timestampUtils.minusMinutes(scheduleEndTime, 30);
            return (timestampUtils.isAfterOrEqual(startTime, scheduleStartTime)) && (timestampUtils.isBeforeOrEqual(startTime, scheduleEndTime));
        });
    }

    private boolean isPatientHasAppointmentAtSameTime(AppointmentDTO appointmentDTO) {
        Long patientId = appointmentDTO.getPatientEntityId();
        if (patientId == null) {
            return false;
        }
        Timestamp startTime = appointmentDTO.getStartTime();
        return appointmentRepository.existsByPatientEntityIdAndStartTimeEquals(patientId, startTime);
    }

    private boolean isDoctorBookedByOtherPatient(AppointmentDTO appointmentDTO) {
        Long doctorId = appointmentDTO.getDoctorEntityId();
        Timestamp startTime = appointmentDTO.getStartTime();
        return appointmentRepository.existsByDoctorEntityIdAndStartTimeEquals(doctorId, startTime);
    }

    private boolean isPatientBelongsToUser(AppointmentDTO appointmentDTO) {
        Long patientId = appointmentDTO.getPatientEntityId();
        Long userId = appointmentDTO.getPatientEntityUserEntityId();
        if (userId == null) {
            return false;
        }
        return patientRepository.existsByIdAndUserEntityId(patientId, userId);
    }

    @Override
    public Map<String, Object> saveAppointment(AppointmentDTO appointmentDTO) {
        if (!isDoctorExists(appointmentDTO)) {
            return Map.of(
                    "success", false,
                    "message", "Doctor not found"
            );
        }
        if (!isDoctorSupportService(appointmentDTO)) {
            return Map.of(
                    "success", false,
                    "message", "Service not found"
            );
        }
        // Validate appointment time
        if (!isStartTimeValid(appointmentDTO)) {
            return Map.of(
                    "success", false,
                    "message", "Invalid appointment time"
            );
        }
        // Check appointment time between staff schedule
        if (!isStartTimeBetweenStaffSchedule(appointmentDTO)) {
            return Map.of(
                    "success", false,
                    "message", "Appointment time is not within staff schedule"
            );
        }
        // Check patient have same appointment time (same patient)
        if (isPatientHasAppointmentAtSameTime(appointmentDTO)) {
            return Map.of(
                    "success", false,
                    "message", "You already have an appointment at this time"
            );
        }
        // Check appointment time duplicate with other appointment (other patient)
        if (isDoctorBookedByOtherPatient(appointmentDTO)) {
            return Map.of(
                    "success", false,
                    "message", "Doctor is already booked at this time"
            );
        }
        // Check if the patient does not belong to the user
        if (!isPatientBelongsToUser(appointmentDTO)) {
            return Map.of(
                    "success", false,
                    "message", "Patient does not belong to the user"
            );
        }
        AppointmentEntity appointmentEntity = appointmentConverter.toEntity(appointmentDTO);
        appointmentEntity.setAppointmentStatus(AppointmentStatus.PENDING);
        appointmentEntity = appointmentRepository.save(appointmentEntity);
        return Map.of(
                "success", true
        );
    }

    @Autowired
    public void setTimestampUtils(TimestampUtils timestampUtils) {
        this.timestampUtils = timestampUtils;
    }
}
