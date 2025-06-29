package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.config.WebConstant;
import org.project.converter.AppointmentApprovalConverter;
import org.project.converter.AppointmentConverter;
import org.project.entity.AppointmentEntity;
import org.project.entity.StaffScheduleEntity;
import org.project.enums.AppointmentStatus;
import org.project.model.dto.AppointmentDTO;
import org.project.model.response.AppointmentApprovalResponse;
import org.project.repository.*;
import org.project.service.AppointmentService;
import org.project.service.PatientService;
import org.project.utils.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {
    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private ServiceRepository serviceRepository;
    private StaffScheduleRepository staffScheduleRepository;
    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private AppointmentConverter appointmentConverter;
    private TimestampUtils timestampUtils;
    private PatientService patientService;
    private AppointmentApprovalConverter appointmentApprovalConverter;

    @Autowired
    public void setAppointmentApprovalConverter(AppointmentApprovalConverter appointmentApprovalConverter) {
        this.appointmentApprovalConverter = appointmentApprovalConverter;
    }

    @Autowired
    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

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
        // Check if the user exists and is active
        if (!isUserExistsAndActive(appointmentDTO)) {
            return Map.of(
                    "success", false,
                    "message", "User does not exist or is not active"
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

    @Override
    public List<AppointmentApprovalResponse> getAppointmentsHaveStatusPendingByHospitalId(Long hospitalId) {
        List<AppointmentEntity> appointmentEntities = appointmentRepository.findByDoctorEntity_StaffEntity_HospitalEntity_IdAndAppointmentStatus(hospitalId, AppointmentStatus.PENDING);

        if (appointmentEntities.isEmpty() || appointmentEntities == null) {
            return Collections.emptyList();
        }

        Map<AppointmentKey, List<Long>> appointmentLookupMap = createAppointmentLookupMap(appointmentEntities);

        return appointmentEntities.stream()
                .map(appointmentEntity -> {
                    List<Long> conflictIds = getConflictAppointmentIds(appointmentEntity, appointmentLookupMap);
                    return appointmentApprovalConverter.convertToResponse(appointmentEntity, conflictIds);
                })
                .collect(Collectors.toList());
    }

    private Map<AppointmentKey, List<Long>> createAppointmentLookupMap(List<AppointmentEntity> appointmentEntities) {

        Map<AppointmentKey, List<Long>> appointmentLookupMap = new HashMap<>();

        for (AppointmentEntity appointmentEntity : appointmentEntities) {
            if (appointmentEntity.getDoctorEntity() == null || appointmentEntity.getStartTime() == null) {
                continue; // Skip if doctor or start time is not set
            }

            AppointmentKey key = new AppointmentKey(appointmentEntity.getDoctorEntity().getId(), appointmentEntity.getStartTime());

            appointmentLookupMap.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(appointmentEntity.getId());
        }
        return appointmentLookupMap;
    }

    private List<Long> getConflictAppointmentIds(AppointmentEntity appointmentEntity, Map<AppointmentKey, List<Long>> appointmentLookupMap) {
        if (appointmentEntity.getDoctorEntity() == null || appointmentEntity.getStartTime() == null) {
            return Collections.emptyList(); // No conflict if doctor or start time is not set
        }

        AppointmentKey key = new AppointmentKey(appointmentEntity.getDoctorEntity().getId(), appointmentEntity.getStartTime());
        return Optional.ofNullable(appointmentLookupMap.get(key))
                .map(ids -> ids.stream()
                        .filter(id -> !id.equals(appointmentEntity.getId()))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private static class AppointmentKey {
        private final Long doctorEntityId;
        private final Timestamp startTime;

        public AppointmentKey(Long doctorEntityId, Timestamp startTime) {
            this.doctorEntityId = doctorEntityId;
            this.startTime = startTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AppointmentKey)) return false;
            AppointmentKey that = (AppointmentKey) o;
            return Objects.equals(doctorEntityId, that.doctorEntityId) &&
                   Objects.equals(startTime, that.startTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(doctorEntityId, startTime);
        }
    }

    @Autowired
    public void setTimestampUtils(TimestampUtils timestampUtils) {
        this.timestampUtils = timestampUtils;
    }
}
