package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.config.WebConstant;
import org.project.converter.AppointmentApprovalConverter;
import org.project.converter.AppointmentConverter;
import org.project.entity.AppointmentEntity;
import org.project.entity.DoctorEntity;
import org.project.entity.StaffEntity;
import org.project.entity.StaffScheduleEntity;
import org.project.enums.AppointmentStatus;
import org.project.exception.ErrorResponse;
import org.project.model.dto.AppointmentDTO;
import org.project.model.dto.ChangeAppointmentDTO;
import org.project.model.response.AppointmentApprovalResponse;
import org.project.model.response.AppointmentAvailableResponse;
import org.project.repository.*;
import org.project.service.AppointmentService;
import org.project.service.StaffService;
import org.project.utils.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
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
    private StaffService staffService;
    private AppointmentApprovalConverter appointmentApprovalConverter;

    @Autowired
    public void setAppointmentApprovalConverter(AppointmentApprovalConverter appointmentApprovalConverter) {
        this.appointmentApprovalConverter = appointmentApprovalConverter;
    }

    @Autowired
    public void setStaffService(StaffService staffService) {
        this.staffService = staffService;
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

    @Override
    public Long countCompletedAppointmentsByDepartment(Long departmentId) {
        return appointmentRepository.countByAppointmentStatusAndServiceEntityDepartmentEntityId(
                AppointmentStatus.COMPLETED, departmentId);
    }

    @Override
    public Long countCompletedAppointments() {
        return appointmentRepository.countByAppointmentStatus(AppointmentStatus.COMPLETED);
    }

    @Override
    public List<AppointmentApprovalResponse> getAppointmentsHaveStatusPendingByStaffId(Long staffId) {
        if (staffId == null) {
            return Collections.emptyList();
        }
        StaffEntity staffEntity = staffService.getStaffByStaffId(staffId);
        List<AppointmentEntity> appointmentEntities = appointmentRepository.findByDoctorEntity_StaffEntity_HospitalEntity_IdAndAppointmentStatus(staffEntity.getHospitalEntity().getId(), AppointmentStatus.PENDING);

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

    @Override
    public Boolean changeStatus(Long appointmentId, AppointmentStatus status, Long schedulingCoordinatorId) {
        if (appointmentId == null || status == null) {
            return false;
        }
        int updatedRows = appointmentRepository.updateAppointmentStatus(appointmentId, status, schedulingCoordinatorId);
        return updatedRows > 0;
    }

    @Override
    public AppointmentAvailableResponse getAvailableAppointmentsByDoctorIdForSuggestion(Long staffId, Long patientId, Timestamp availableTimestamp, int maxDays) {
        AppointmentAvailableResponse response = new AppointmentAvailableResponse();
        if (staffId == null || patientId == null || availableTimestamp == null) {
            response.setAvailableTimes(Collections.emptyList());
            return response;
        }
        Optional<DoctorEntity> doctorEntityOptional = doctorRepository.findById(staffId);
        if (doctorEntityOptional.isEmpty()) {
            response.setAvailableTimes(Collections.emptyList());
            return response; // Doctor not found
        }
        List<Timestamp> availableTimes = getAvailableTimes(staffId, patientId, availableTimestamp, maxDays);
        if (availableTimes == null || availableTimes.isEmpty()) {
            response.setAvailableTimes(Collections.emptyList());
        } else {
            response.setAvailableTimes(availableTimes);
        }
        response.setStaffId(staffId);
        String doctorName = doctorEntityOptional.get().getStaffEntity().getFullName();
        response.setStaffName(doctorName);
        response.setAvailableTimes(availableTimes);
        return response;
    }

    @Override
    public Boolean changeAppointment(ChangeAppointmentDTO changeAppointmentDTO) {
        if (changeAppointmentDTO == null || changeAppointmentDTO.getAppointmentId() == null) {
            return false; // Invalid input
        }
        AppointmentEntity appointmentEntity = appointmentRepository.findById(changeAppointmentDTO.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + changeAppointmentDTO.getAppointmentId()));

        appointmentEntity = appointmentApprovalConverter.convertToEntity(appointmentEntity, changeAppointmentDTO);

        // Save updated appointment
        return appointmentRepository.save(appointmentEntity).getId() != null;
    }

    private List<Timestamp> getAvailableTimes(Long staffId, Long patientId, Timestamp availableTimestamp, int maxDays) {
        TimestampUtils timestampUtils = new TimestampUtils();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<Timestamp> availableSlots = new ArrayList<>();
        final int SLOT_DURATION_MINUTES = 15;

        Timestamp currentDay = timestampUtils.getStartOfDay(availableTimestamp);

        for (int day = 0; day < maxDays && availableSlots.size() < 10; day++) {
            Timestamp startOfDay = timestampUtils.getStartOfDay(currentDay);
            Timestamp endOfDay = timestampUtils.getEndOfDay(currentDay);

            boolean isToday = timestampUtils.isSameDay(startOfDay, now);
            Timestamp filterStart = isToday ? now : startOfDay;

            List<StaffScheduleEntity> scheduleEntities = staffScheduleRepository.findByStaffEntityIdAndAvailableDate(
                    staffId, new Date(currentDay.getTime())
            );

            if (scheduleEntities.isEmpty()) {
                currentDay = timestampUtils.plusDays(currentDay, 1);
                continue;
            }

            Set<Timestamp> busySlots = new HashSet<>();

            List<AppointmentEntity> conflictingAppointments = appointmentRepository.findConflictingAppointments(
                    staffId, patientId, startOfDay, endOfDay);

            for (AppointmentEntity appointmentEntity : conflictingAppointments) {
                Timestamp appointmentStart = appointmentEntity.getStartTime();
                Timestamp appointmentEnd = timestampUtils.plusMinutes(
                        appointmentStart,
                        appointmentEntity.getDurationMinutes() // Dùng duration thực
                );

                Timestamp slot = appointmentStart;
                while (slot.before(appointmentEnd)) {
                    busySlots.add(slot);
                    slot = timestampUtils.plusMinutes(slot, SLOT_DURATION_MINUTES);
                }
            }

            for (StaffScheduleEntity staffScheduleEntity : scheduleEntities) {
                Timestamp slot = staffScheduleEntity.getStartTime();
                Timestamp endSlot = staffScheduleEntity.getEndTime();

                while (slot.before(endSlot)) {
                    // SỬA: bỏ điều kiện slot.after(now)
                    if (!slot.before(filterStart) && !busySlots.contains(slot)) {
                        availableSlots.add(slot);
                        if (availableSlots.size() >= 10) {
                            break;
                        }
                    }
                    slot = timestampUtils.plusMinutes(slot, SLOT_DURATION_MINUTES);
                }
                if (availableSlots.size() >= 10) {
                    break;
                }
            }

            // Thoát ngay nếu đã đủ slot
            if (availableSlots.size() >= 10) {
                break;
            }

            currentDay = timestampUtils.plusDays(currentDay, 1);
        }
        return availableSlots.isEmpty() ? null : availableSlots;
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
