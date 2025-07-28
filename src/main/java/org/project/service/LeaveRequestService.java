package org.project.service;

import org.project.model.dto.LeaveRequestDTO;
import org.project.model.response.LeaveBalanceResponse;
import org.project.model.response.LeaveRequestResponse;
import org.project.model.response.LeaveRequestStatisticResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

public interface LeaveRequestService {
    boolean saveLeaveRequest(LeaveRequestDTO leaveRequestDTO);

    List<LeaveRequestResponse> getLeaveRequestsByStaffId(Long staffId);

    Page<LeaveRequestResponse> getLeaveRequestByManagerId(Long managerId, int index, int size, String status, String staffName, String leaveType);

    LeaveRequestStatisticResponse getLeaveRequestStatisticByManagerId(Long managerId);

    Page<LeaveRequestResponse> getLeaveRequestByStaffId(Long staffId, int index, int size, String status, String leaveType);

    LeaveBalanceResponse getLeaveBalanceByStaffIdAndYear(Long staffId, Year year);

    BigDecimal calculateRequestedDays(LeaveRequestDTO leaveRequestDTO);

    BigDecimal getLeaveDayLeft(LeaveRequestDTO leaveRequestDTO);

    LeaveRequestResponse getLeaveRequestById(Long leaveRequestId);
}
