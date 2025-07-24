package org.project.controller;

import org.project.model.response.LeaveBalanceResponse;
import org.project.model.response.LeaveRequestResponse;
import org.project.model.response.LeaveRequestStatisticResponse;
import org.project.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @GetMapping("/leave-staff/{staffId}")
    public String showLeaveRequestPage(@PathVariable Long staffId, Model model) {
        Year currentYear = Year.now();
        List<LeaveRequestResponse> leaveRequests = leaveRequestService.getLeaveRequestsByStaffId(staffId);
        LeaveBalanceResponse leaveBalanceResponse = leaveRequestService.getLeaveBalanceByStaffIdAndYear(staffId, currentYear);
        model.addAttribute("staffId", staffId);
        model.addAttribute("leaveBalance", leaveBalanceResponse);
        model.addAttribute("leaveRequests", leaveRequests);
        return "frontend/leave"; // Adjust the view name as necessary
    }

    @GetMapping("/leave-manager/{managerId}")
    public String showManagerLeaveRequests(@PathVariable Long managerId,
                                           @RequestParam(defaultValue = "0") int pageIndex,
                                           Model model
                                        ) {
        Page<LeaveRequestResponse> leaveRequests = leaveRequestService.getLeaveRequestByManagerId(managerId, pageIndex, 8);
        List<LeaveRequestResponse> leaveRequestResponses = leaveRequests.getContent();
        LeaveRequestStatisticResponse leaveRequestStatisticResponse = leaveRequestService.getLeaveRequestStatisticByManagerId(managerId);
        model.addAttribute("managerId", managerId);
        model.addAttribute("leaveRequestStatistic", leaveRequestStatisticResponse);
        model.addAttribute("leaveRequests", leaveRequestResponses);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("totalPages", leaveRequests.getTotalPages());
        return "frontend/leave-manager"; // Adjust the view name as necessary
    }

    @GetMapping("/leave-staff-list/{staffId}")
    public String showStaffLeaveRequests(@PathVariable Long staffId,
                                         @RequestParam(defaultValue = "0") int pageIndex,
                                         Model model) {
        Page<LeaveRequestResponse> leaveRequests = leaveRequestService.getLeaveRequestByStaffId(staffId, pageIndex, 8);
        List<LeaveRequestResponse> leaveRequestResponses = leaveRequests.getContent();
        model.addAttribute("staffId", staffId);
        model.addAttribute("leaveRequests", leaveRequestResponses);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("totalPages", leaveRequests.getTotalPages());
        return "frontend/staff-leave-list"; // Adjust the view name as necessary
    }
}
