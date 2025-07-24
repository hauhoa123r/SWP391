package org.project.service.impl;

import org.project.converter.LeaveBalanceConverter;
import org.project.converter.LeaveRequestConverter;
import org.project.entity.LeaveBalanceEntity;
import org.project.entity.LeaveRequestEntity;
import org.project.enums.LeaveStatus;
import org.project.enums.LeaveType;
import org.project.exception.LeaveLeftNotEnoughException;
import org.project.model.dto.LeaveRequestDTO;
import org.project.model.response.LeaveBalanceResponse;
import org.project.model.response.LeaveRequestResponse;
import org.project.model.response.LeaveRequestStatisticResponse;
import org.project.repository.AppointmentRepository;
import org.project.repository.LeaveBalanceRepository;
import org.project.repository.LeaveRequestRepository;
import org.project.service.LeaveRequestService;
import org.project.service.StaffService;
import org.project.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {


    private LeaveRequestConverter leaveRequestConverter;

    private StaffService staffService;

    private LeaveRequestRepository leaveRequestRepository;

    private PageUtils pageUtils;

    private LeaveBalanceRepository leaveBalanceRepository;

    private LeaveBalanceConverter leaveBalanceConverter;

    private AppointmentRepository appointmentRepository;

    @Autowired
    public void setAppointmentRepository(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Autowired
    public void setLeaveBalanceConverter(LeaveBalanceConverter leaveBalanceConverter) {
        this.leaveBalanceConverter = leaveBalanceConverter;
    }

    @Autowired
    public void setLeaveBalanceRepository(LeaveBalanceRepository leaveBalanceRepository) {
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    @Autowired
    public void setLeaveRequestConverter(LeaveRequestConverter leaveRequestConverter) {
        this.leaveRequestConverter = leaveRequestConverter;
    }

    @Autowired
    public void setStaffService(StaffService staffService) {
        this.staffService = staffService;
    }

    @Autowired
    public void setLeaveRequestRepository(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Autowired
    public void setPageUtils(PageUtils pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Override
    public boolean saveLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        try {
            if (staffService.isStaffExist(leaveRequestDTO.getStaffId())) {
                if (hasLeaveAvailable(leaveRequestDTO).compareTo(BigDecimal.ZERO) >= 0) {
                    LeaveRequestEntity leaveRequestEntity = leaveRequestConverter.toEntity(leaveRequestDTO);

                    leaveRequestEntity.setStaffEntity(staffService.getStaffByStaffId(leaveRequestDTO.getStaffId()));

                    leaveRequestEntity.setApprovedBy(staffService.getManagerByStaffId(leaveRequestDTO.getStaffId()));

                    if (leaveRequestDTO.getSubstituteStaffId() != null) {
                        if (staffService.isStaffExist(leaveRequestDTO.getSubstituteStaffId())) {
                            leaveRequestEntity.setStaffSubstitute(staffService.getStaffByStaffId(leaveRequestDTO.getSubstituteStaffId()));
                        } else {
                            leaveRequestEntity.setStaffSubstitute(null);
                        }
                    }
                    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                    leaveRequestEntity.setCreatedAt(currentTimestamp);

                    if (leaveRequestRepository.save(leaveRequestEntity) != null) {
                        updateLeaveBalance(leaveRequestDTO.getStaffId()
                                , leaveRequestDTO.getStartDate()
                                , leaveRequestDTO.getEndDate()
                                , LeaveType.valueOf(leaveRequestDTO.getLeaveType())
                                , "CREATE");
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    throw new LeaveLeftNotEnoughException("Staff with ID " + leaveRequestDTO.getStaffId() + " not enough leave balance");
                }
            } else {
                throw new IllegalArgumentException("Staff with ID " + leaveRequestDTO.getStaffId() + " does not exist.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving leave request: " + e.getMessage(), e);
        }
    }

    public void updateLeaveBalance(Long staffId, Timestamp startTime, Timestamp endTime, LeaveType type, String status) {
        Year year = Year.now();
        BigDecimal req = calculateLeaveDays(startTime, endTime);
        LeaveBalanceEntity leaveBalanceEntity = leaveBalanceRepository.findByStaffEntity_IdAndYearAndLeaveType(staffId, year, type);
        if ("CREATE".equals(status)) {
            leaveBalanceEntity.setPendingBalance(leaveBalanceEntity.getPendingBalance().add(req));
        } else if ("APPROVAL".equals(status)) {
            leaveBalanceEntity.setPendingBalance(leaveBalanceEntity.getPendingBalance().subtract(req));
            leaveBalanceEntity.setUsedBalance(leaveBalanceEntity.getUsedBalance().add(req));
        } else if ("REJECT".equals(status)) {
            leaveBalanceEntity.setPendingBalance(leaveBalanceEntity.getPendingBalance().subtract(req));
        }
        leaveBalanceRepository.save(leaveBalanceEntity);
    }

    @Override
    public List<LeaveRequestResponse> getLeaveRequestsByStaffId(Long staffId) {
        if (staffService.isStaffExist(staffId)) {
            List<LeaveRequestEntity> leaveRequestEntities = leaveRequestRepository.findTop5ByStaffEntity_IdOrderByCreatedAtDesc(staffId);
            List<LeaveRequestResponse> leaveRequestResponses = new ArrayList<>();
            for (LeaveRequestEntity leaveRequestEntity : leaveRequestEntities) {
                LeaveRequestResponse leaveRequestResponse = leaveRequestConverter.toResponse(leaveRequestEntity);

                leaveRequestResponse = convertTotalDaysAndHalfDayType(leaveRequestEntity, leaveRequestResponse);

                leaveRequestResponses.add(leaveRequestResponse);
            }
            return leaveRequestResponses;
        } else {
            throw new IllegalArgumentException("Staff with ID " + staffId + " does not exist.");
        }
    }

    @Override
    public Page<LeaveRequestResponse> getLeaveRequestByManagerId(Long managerId, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<LeaveRequestEntity> leaveRequestEntities = leaveRequestRepository.findFutureLeaveRequestByManager(managerId, pageable);

        Page<LeaveRequestResponse> leaveRequestResponses = leaveRequestEntities.map(entity -> {
            LeaveRequestResponse response = leaveRequestConverter.toResponse(entity);
            response = convertTotalDaysAndHalfDayType(entity, response);
            return response;
        });
        return leaveRequestResponses;
    }

    @Override
    public LeaveRequestStatisticResponse getLeaveRequestStatisticByManagerId(Long managerId) {
        LeaveRequestStatisticResponse response = new LeaveRequestStatisticResponse();
        response.setTotalLeaveRequests(leaveRequestRepository.countByApprovedByManager_IdAndStartDateAfter(managerId, new Timestamp(System.currentTimeMillis())));
        response.setPendingLeaveRequests(leaveRequestRepository.countByApprovedByManager_IdAndStartDateAfterAndStatus(managerId, new Timestamp(System.currentTimeMillis()), LeaveStatus.PENDING));
        response.setRejectedLeaveRequests(leaveRequestRepository.countByApprovedByManager_IdAndStartDateAfterAndStatus(managerId, new Timestamp(System.currentTimeMillis()), LeaveStatus.REJECTED));
        response.setApprovedLeaveRequests(leaveRequestRepository.countByApprovedByManager_IdAndStartDateAfterAndStatus(managerId, new Timestamp(System.currentTimeMillis()), LeaveStatus.APPROVED));

        return response;
    }

    @Override
    public Page<LeaveRequestResponse> getLeaveRequestByStaffId(Long staffId, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);

        Page<LeaveRequestEntity> leaveRequestEntities = leaveRequestRepository.findAllByStaffEntity_IdOrderByCreatedAtDesc(staffId, pageable);

        Page<LeaveRequestResponse> leaveRequestResponses = leaveRequestEntities.map(entity -> {
            LeaveRequestResponse response = leaveRequestConverter.toResponse(entity);
            response = convertTotalDaysAndHalfDayType(entity, response);
            return response;
        });

        return leaveRequestResponses;
    }

    @Override
    public LeaveBalanceResponse getLeaveBalanceByStaffIdAndYear(Long staffId, Year year) {
        List<LeaveBalanceEntity> leaveBalanceEntities = leaveBalanceRepository.findAllByStaffEntity_IdAndYear(staffId, year);
        LeaveBalanceResponse leaveBalanceResponse = leaveBalanceConverter.toResponse(leaveBalanceEntities);
        return leaveBalanceResponse;
    }

    @Override
    public BigDecimal calculateRequestedDays(LeaveRequestDTO leaveRequestDTO) {

        BigDecimal requested = calculateLeaveDays(leaveRequestDTO.getStartDate(), leaveRequestDTO.getEndDate());

        return requested.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public BigDecimal getLeaveDayLeft(LeaveRequestDTO dto) {
        Year year = Year.now();
        LeaveBalanceEntity bal = leaveBalanceRepository
                .findByStaffEntity_IdAndYearAndLeaveType(
                        dto.getStaffId(), year, LeaveType.valueOf(dto.getLeaveType())
                );
        BigDecimal total = bal.getTotalEntitlement() != null ? bal.getTotalEntitlement() : BigDecimal.ZERO;
        BigDecimal used = bal.getUsedBalance() != null ? bal.getUsedBalance() : BigDecimal.ZERO;
        BigDecimal pending = bal.getPendingBalance() != null ? bal.getPendingBalance() : BigDecimal.ZERO;

        return total.subtract(used).subtract(pending).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public LeaveRequestResponse getLeaveRequestById(Long leaveRequestId) {
        LeaveRequestEntity leaveRequestEntity = leaveRequestRepository.getById(leaveRequestId);

        LeaveRequestResponse leaveRequestResponse = leaveRequestConverter.toResponse(leaveRequestEntity);

        leaveRequestResponse = convertTotalDaysAndHalfDayType(leaveRequestEntity, leaveRequestResponse);

        return leaveRequestResponse;
    }

    public BigDecimal hasLeaveAvailable(LeaveRequestDTO dto) {
        BigDecimal left = getLeaveDayLeft(dto);
        BigDecimal req = calculateRequestedDays(dto);

        return left.subtract(req).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal calculateLeaveDays(Timestamp startDate, Timestamp endDate) {
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            throw new IllegalArgumentException("Invalid date range for leave days calculation.");
        }

        LocalDateTime start = startDate.toLocalDateTime();
        LocalDateTime end = endDate.toLocalDateTime();

        long totalMinutes = ChronoUnit.MINUTES.between(start, end);
        if (totalMinutes < 0) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        BigDecimal WORKING_HOURS_PER_DAY = BigDecimal.valueOf(8L * 60L);

        BigDecimal leaveDays = BigDecimal
                .valueOf(totalMinutes)
                .divide(WORKING_HOURS_PER_DAY, 2, RoundingMode.HALF_UP);

        return leaveDays;
    }

    public String halfDayOrFullDay(BigDecimal leaveDays) {
        if (leaveDays.compareTo(BigDecimal.ZERO) <= 0) {
            return "Invalid leave days";
        } else if (leaveDays.compareTo(BigDecimal.ONE) < 0) {
            return "Half Day";
        } else {
            return "Full Day";
        }
    }

    public String amOrPm(Timestamp startDate, Timestamp endDate) {
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            throw new IllegalArgumentException("Invalid date range for AM/PM calculation.");
        }

        LocalDateTime start = startDate.toLocalDateTime();
        LocalDateTime end = endDate.toLocalDateTime();

        if (start.getHour() < 12 && end.getHour() < 12) {
            return "AM";
        } else if (start.getHour() >= 12 && end.getHour() >= 12) {
            return "PM";
        } else {
            return "Mixed";
        }
    }

    public LeaveRequestResponse convertTotalDaysAndHalfDayType(LeaveRequestEntity leaveRequestEntity, LeaveRequestResponse leaveRequestResponse) {
        leaveRequestResponse.setTotalDays(calculateLeaveDays(leaveRequestEntity.getStartDate(), leaveRequestEntity.getEndDate()).toString());

        String halfOrFullDay = halfDayOrFullDay(calculateLeaveDays(leaveRequestEntity.getStartDate(), leaveRequestEntity.getEndDate()));
        if ("Half Day".equals(halfOrFullDay)) {
            if ("AM".equals(amOrPm(leaveRequestEntity.getStartDate(), leaveRequestEntity.getEndDate()))) {
                leaveRequestResponse.setHalfDayType("Buổi sáng");
            } else {
                leaveRequestResponse.setHalfDayType("Buổi chiều");
            }
        } else {
            leaveRequestResponse.setHalfDayType("Toàn ngày");
        }
        return leaveRequestResponse;
    }

    @Override
    public boolean changeStatus(Long id, LeaveStatus status, String reason) {
        if ("APPROVED".equals(status)){
            transferAppointments(id);
        }
        return leaveRequestRepository.updateLeaveRequestStatus(id, status, reason) > 0;
    }

    @Override
    public void transferAppointments(Long leaveRequestId) {
        LeaveRequestEntity leaveRequestEntity = leaveRequestRepository.getById(leaveRequestId);
        String halfDayType = amOrPm(leaveRequestEntity.getStartDate(), leaveRequestEntity.getEndDate());
        if (leaveRequestEntity.getStaffSubstitute() != null) {
            if ("AM".equals(halfDayType)) {
                appointmentRepository.transferMorningShiftAppointments(leaveRequestEntity.getStaffEntity().getId(),
                        leaveRequestEntity.getStaffSubstitute().getId(),
                        leaveRequestEntity.getStartDate());
            } else if ("PM".equals(halfDayType)) {
                appointmentRepository.transferAfternoonShiftAppointments(leaveRequestEntity.getStaffEntity().getId(),
                        leaveRequestEntity.getStaffSubstitute().getId(),
                        leaveRequestEntity.getStartDate());
            } else {
                appointmentRepository.transferFullDayAppointments(leaveRequestEntity.getStaffEntity().getId(),
                        leaveRequestEntity.getStaffSubstitute().getId(),
                        leaveRequestEntity.getStartDate(),
                        leaveRequestEntity.getEndDate());
            }
        }
    }

}
