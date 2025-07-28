package org.project.controller;

import org.project.model.response.LeaveBalanceResponse;
import org.project.model.response.LeaveRequestResponse;
import org.project.model.response.LeaveRequestStatisticResponse;
import org.project.security.AccountDetails;
import org.project.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;
import java.util.List;

@Controller
public class LeaveRequestController {

    private LeaveRequestService leaveRequestService;

    @Autowired
    public void setLeaveRequestService(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @GetMapping("staff/leave-staff")
    public String showLeaveRequestPage(@AuthenticationPrincipal AccountDetails accountDetails, Model model) {
        Year currentYear = Year.now();
        Long staffId = accountDetails.getUserEntity().getStaffEntity().getId();
        List<LeaveRequestResponse> leaveRequests = leaveRequestService.getLeaveRequestsByStaffId(staffId);
        LeaveBalanceResponse leaveBalanceResponse = leaveRequestService.getLeaveBalanceByStaffIdAndYear(staffId, currentYear);
        if(accountDetails.getUserEntity().getStaffEntity().getManager() != null) {
            model.addAttribute("staffRole", "STAFF");
        } else {
            model.addAttribute("staffRole", "MANAGER");
        }
        model.addAttribute("avatarUrl", accountDetails.getUserEntity().getStaffEntity().getAvatarUrl());
        model.addAttribute("activeMenu", "leaveStaff");
        model.addAttribute("staffId", staffId);
        model.addAttribute("leaveBalance", leaveBalanceResponse);
        model.addAttribute("leaveRequests", leaveRequests);
        return "dashboard-staff/leave"; // Adjust the view name as necessary
    }

    @GetMapping("staff/manager/leave-manager")
    public String showManagerLeaveRequests(@AuthenticationPrincipal AccountDetails accountDetails,
                                           @RequestParam(defaultValue = "0") int pageIndex,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(required = false) String staffName,
                                           @RequestParam(required = false) String leaveType,
                                           Model model
                                        ) {
        Long managerId = accountDetails.getUserEntity().getStaffEntity().getId();
        if (accountDetails.getUserEntity().getStaffEntity().getManager() != null) {
            return "redirect:/staff/leave-staff";
        }
        Page<LeaveRequestResponse> leaveRequests = leaveRequestService.getLeaveRequestByManagerId(managerId, pageIndex, 8, status, staffName, leaveType);
        List<LeaveRequestResponse> leaveRequestResponses = leaveRequests.getContent();
        LeaveRequestStatisticResponse leaveRequestStatisticResponse = leaveRequestService.getLeaveRequestStatisticByManagerId(managerId);
        model.addAttribute("avatarUrl", accountDetails.getUserEntity().getStaffEntity().getAvatarUrl());
        model.addAttribute("managerId", managerId);
        model.addAttribute("activeMenu", "leaveManager");
        model.addAttribute("leaveRequestStatistic", leaveRequestStatisticResponse);
        model.addAttribute("leaveRequests", leaveRequestResponses);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("totalPages", leaveRequests.getTotalPages());
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentStaffName", staffName);
        model.addAttribute("currentLeaveType", leaveType);
        return "dashboard-staff/leave-manager";
    }

    @GetMapping("staff/leave-staff-list")
    public String showStaffLeaveRequests(@AuthenticationPrincipal AccountDetails accountDetails,
                                         @RequestParam(defaultValue = "0") int pageIndex,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String leaveType,
                                         Model model) {
        Year currentYear = Year.now();
        Long staffId = accountDetails.getUserEntity().getStaffEntity().getId();
        Page<LeaveRequestResponse> leaveRequests = leaveRequestService.getLeaveRequestByStaffId(staffId, pageIndex, 8, status, leaveType);
        LeaveBalanceResponse leaveBalanceResponse = leaveRequestService.getLeaveBalanceByStaffIdAndYear(staffId, currentYear);
        List<LeaveRequestResponse> leaveRequestResponses = leaveRequests.getContent();
        model.addAttribute("avatarUrl", accountDetails.getUserEntity().getStaffEntity().getAvatarUrl());
        model.addAttribute("staffId", staffId);
        model.addAttribute("leaveBalance", leaveBalanceResponse);
        model.addAttribute("leaveRequests", leaveRequestResponses);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentLeaveType", leaveType);
        model.addAttribute("totalPages", leaveRequests.getTotalPages());
        return "dashboard-staff/staff-leave-list"; // Adjust the view name as necessary
    }
}
