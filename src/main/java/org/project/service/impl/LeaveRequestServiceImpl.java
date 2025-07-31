package org.project.service.impl;

import jakarta.persistence.criteria.JoinType;
import org.project.converter.LeaveBalanceConverter;
import org.project.converter.LeaveRequestConverter;
import org.project.entity.LeaveBalanceEntity;
import org.project.entity.LeaveRequestEntity;
import org.project.enums.LeaveStatus;
import org.project.enums.LeaveType;
import org.project.enums.operation.ComparisonOperator;
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
import org.project.utils.specification.SpecificationUtils;
import org.project.utils.specification.search.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    private SpecificationUtils<LeaveRequestEntity> specificationUtils;

    @Autowired
    public void setSpecificationUtils(SpecificationUtils<LeaveRequestEntity> specificationUtils) {
        this.specificationUtils = specificationUtils;
    }

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

                    if (leaveRequestEntity.getStaffEntity().getManager() != null) {
                        leaveRequestEntity.setApprovedBy(staffService.getManagerByStaffId(leaveRequestDTO.getStaffId()));
                    } else {
                        leaveRequestEntity.setApprovedBy(staffService.getStaffByStaffId(leaveRequestDTO.getStaffId()));
                    }

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
                        if ("ANNUAL_LEAVE".equals(leaveRequestDTO.getLeaveType()) || "SICK_LEAVE".equals(leaveRequestDTO.getLeaveType()) || "STUDY_LEAVE".equals(leaveRequestDTO.getLeaveType())) {
                            updateLeaveBalance(leaveRequestDTO.getStaffId()
                                    , leaveRequestDTO.getStartDate()
                                    , leaveRequestDTO.getEndDate()
                                    , LeaveType.valueOf(leaveRequestDTO.getLeaveType())
                                    , "CREATE");
                        }
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
        if(!staffService.isStaffExist(staffId)) {
            return Collections.emptyList();
        }

        List<LeaveRequestEntity> entities = leaveRequestRepository.findTop5ByStaffEntity_IdOrderByCreatedAtDesc(staffId);
        return entities.stream()
                .map(ent -> {
                    LeaveRequestResponse resp = leaveRequestConverter.toResponse(ent);
                    return convertTotalDaysAndHalfDayType(ent, resp);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<LeaveRequestResponse> getLeaveRequestByManagerId(Long managerId, int index, int size, String status, String staffName, String leaveType) {
        Pageable pageable = pageUtils.getPageable(index, size);

        List<SearchCriteria> criteriaList = new ArrayList<>();

        criteriaList.add(new SearchCriteria(
                "approvedBy.id"
                , ComparisonOperator.EQUALS, managerId, JoinType.INNER ));

        criteriaList.add(new SearchCriteria("startDate", ComparisonOperator.GREATER_THAN, new Timestamp(System.currentTimeMillis()), JoinType.INNER));

        if (status != null && !status.isEmpty()) {
            criteriaList.add(new SearchCriteria("status", ComparisonOperator.EQUALS, status, JoinType.INNER));
        }

        if (staffName != null && !staffName.isEmpty()) {
            criteriaList.add(new SearchCriteria("staffEntity.fullName", ComparisonOperator.LIKE, "%" + staffName + "%", JoinType.INNER));
        }

        if (leaveType != null && !leaveType.isEmpty()) {
            criteriaList.add(new SearchCriteria("leaveTypeEntity.name", ComparisonOperator.EQUALS, leaveType, JoinType.INNER));
        }

        Page<LeaveRequestEntity> leaveRequestEntities = leaveRequestRepository.findAll(
                specificationUtils.reset().getSearchSpecifications(criteriaList),
                pageable
        );

        return leaveRequestEntities.map(entity -> {
            LeaveRequestResponse response = leaveRequestConverter.toResponse(entity);
            return convertTotalDaysAndHalfDayType(entity, response);
        });
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
    public Page<LeaveRequestResponse> getLeaveRequestByStaffId(Long staffId, int index, int size, String status, String leaveType) {
        Pageable pageable = pageUtils.getPageable(index, size);

        List<SearchCriteria> criteriaList = new ArrayList<>();

        criteriaList.add(new SearchCriteria("staffEntity.id", ComparisonOperator.EQUALS, staffId, JoinType.INNER));

        if (status != null && !status.isEmpty()) {
            criteriaList.add(new SearchCriteria("status", ComparisonOperator.EQUALS, status, JoinType.INNER));
        }

        if (leaveType != null && !leaveType.isEmpty()) {
            criteriaList.add(new SearchCriteria("leaveTypeEntity.name", ComparisonOperator.EQUALS, leaveType, JoinType.INNER));
        }

        Page<LeaveRequestEntity> leaveRequestEntities = leaveRequestRepository.findAll(
                specificationUtils.reset().getSearchSpecifications(criteriaList),
                pageable
        );

        return leaveRequestEntities.map(entity -> {
            LeaveRequestResponse response = leaveRequestConverter.toResponse(entity);
            return convertTotalDaysAndHalfDayType(entity, response);
        });
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
        if ("ANNUAL_LEAVE".equals(dto.getLeaveType()) || "SICK_LEAVE".equals(dto.getLeaveType()) || "STUDY_LEAVE".equals(dto.getLeaveType())) {
            BigDecimal left = getLeaveDayLeft(dto);
            BigDecimal req = calculateRequestedDays(dto);
            return left.subtract(req).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ONE;
    }

    public BigDecimal calculateLeaveDays(Timestamp startDate, Timestamp endDate) {
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            throw new IllegalArgumentException("Invalid date range for leave days calculation.");
        }

        LocalDateTime start = startDate.toLocalDateTime();
        LocalDateTime end = endDate.toLocalDateTime();

        if (start.toLocalDate().equals(end.toLocalDate())) {
            boolean isFullDay = isFullDayLeave(start, end);
            return isFullDay ? BigDecimal.ONE : new BigDecimal("0.5");
        }

        long fullDays = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
        return BigDecimal.valueOf(fullDays + 1);
    }

    private boolean isFullDayLeave(LocalDateTime start, LocalDateTime end) {

        int MORNING_END_HOUR = 12;
        int AFTERNOON_START_HOUR = 13;

        return start.getHour() < MORNING_END_HOUR &&
                end.getHour() >= AFTERNOON_START_HOUR;
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
