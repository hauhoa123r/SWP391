package org.project.controller;

import org.project.model.response.AppointmentApprovalResponse;
import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/appointment")
public class AppointmentApprovalController {

    private AppointmentService appointmentService;

    @Autowired
    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/showPending/{hospitalId}")
    public String showPendingAppointmentsByHospitalId(@PathVariable Long hospitalId,
                                                        Model model) {
        List<AppointmentApprovalResponse> pendingAppointments = appointmentService.getAppointmentsHaveStatusPendingByHospitalId(hospitalId);

        model.addAttribute("pendingAppointments", pendingAppointments);
        return "frontend/appointment-approval";
    }
}
