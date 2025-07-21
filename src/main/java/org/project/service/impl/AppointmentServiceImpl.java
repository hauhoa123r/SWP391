package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.config.WebConstant;
import org.project.converter.AppointmentConverter;
import org.project.entity.AppointmentEntity;
import org.project.entity.StaffScheduleEntity;
import org.project.enums.AppointmentStatus;
import org.project.exception.ErrorResponse;
import org.project.model.dto.AppointmentDTO;
import org.project.repository.*;
import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    public void setDoctorRepository(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Autowired
    private toAppointmentsResponse toAppoint;
    public void setServiceRepository(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Autowired
    private toAppointmentDetailsResponse toAppointDetails;
    public void setStaffScheduleRepository(StaffScheduleRepository staffScheduleRepository) {
        this.staffScheduleRepository = staffScheduleRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return serviceRepository.existsByIdAndProductEntityProductStatusAndDepartmentEntityStaffEntitiesId(serviceId, WebConstant.PRODUCT_STATUS_ACTIVE, doctorId);
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

    private boolean isUserExistsAndActive(AppointmentDTO appointmentDTO) {
        Long userId = appointmentDTO.getPatientEntityUserEntityId();
        if (userId == null) {
            return false;
        }
        return userRepository.existsByIdAndUserStatus(userId, WebConstant.USER_STATUS_ACTIVE);
    }

    private boolean isPatientBelongsToUser(AppointmentDTO appointmentDTO) {
        Long patientId = appointmentDTO.getPatientEntityId();
        Long userId = appointmentDTO.getPatientEntityUserEntityId();
        return patientRepository.existsByIdAndUserEntityId(patientId, userId);
    }

    @Override
    public List<AppointmentsResponse> getAllAppointments(Long doctorId) {
        List<AppointmentsEntity> appointments = appointmentRepository.findByDoctorId(doctorId);
        return appointments.stream().map(appointment ->
            toAppoint.toAppointmentResponse(appointment)
        ).collect(Collectors.toList());
    public void saveAppointment(AppointmentDTO appointmentDTO) {
        if (!isDoctorExists(appointmentDTO)) {
            throw new ErrorResponse("Doctor not found");
        }
        if (!isDoctorSupportService(appointmentDTO)) {
            throw new ErrorResponse("Doctor does not support this service");
        }
        // Validate appointment time
        if (!isStartTimeValid(appointmentDTO)) {
            throw new ErrorResponse("Invalid appointment time");
        }
        // Check appointment time between staff schedule
        if (!isStartTimeBetweenStaffSchedule(appointmentDTO)) {
            throw new ErrorResponse("Appointment time is not within staff schedule");
        }
        // Check patient have same appointment time (same patient)
        if (isPatientHasAppointmentAtSameTime(appointmentDTO)) {
            throw new ErrorResponse("You already have an appointment at this time");
        }
        // Check appointment time duplicate with other appointment (other patient)
        if (isDoctorBookedByOtherPatient(appointmentDTO)) {
            throw new ErrorResponse("Doctor is already booked at this time");
        }
        // Check if the user exists and is active
        if (!isUserExistsAndActive(appointmentDTO)) {
            throw new ErrorResponse("User does not exist or is not active");
        }
        // Check if the patient does not belong to the user
        if (!isPatientBelongsToUser(appointmentDTO)) {
            throw new ErrorResponse("Patient does not belong to the user");
        }
        AppointmentEntity appointmentEntity = appointmentConverter.toEntity(appointmentDTO);
        appointmentEntity.setAppointmentStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appointmentEntity);
    }

    @Autowired
    public void setTimestampUtils(TimestampUtils timestampUtils) {
        this.timestampUtils = timestampUtils;
    }
}
