package org.project.controller;

import org.project.model.dto.AppointmentDTO;
import org.project.model.response.AppointmentCustomerResponse;
import org.project.security.AccountDetails;
import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AppointmentController {

    private AppointmentService appointmentService;

    @Autowired
    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/patient/appointment")
    public String getAppointmentPage() {
        return "/frontend/appointment";
    }

    @GetMapping(value = {"/admin/appointment", "/admin"})
    public String getAppointmentAdminPage(ModelMap modelMap) {
        modelMap.addAttribute("appointmentDTO", new AppointmentDTO());
        return "/dashboard/appointment";
    }

    @GetMapping("/patient/list-appointment")
    public String getListAppointmentPage(@AuthenticationPrincipal AccountDetails accountDetails,
                                         @RequestParam(defaultValue = "0") int pageIndex,
                                         @RequestParam(required = false) String status,
                                         Model model) {
        Page<AppointmentCustomerResponse> appointmentCustomerResponses = appointmentService.getAppointmentByUserId(accountDetails.getUserEntity().getId(), pageIndex, 6, status);

        List<AppointmentCustomerResponse> appointmentCustomerResponseList = appointmentCustomerResponses.getContent();
        model.addAttribute("appointments", appointmentCustomerResponseList);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("totalPages", appointmentCustomerResponses.getTotalPages());
        model.addAttribute("currentStatus", status);
        return "/frontend/appointment-list";
    }
}
