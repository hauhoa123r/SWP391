package org.project.controller;

import org.project.model.response.AppointmentDashboardCustomerResponse;
import org.project.model.response.DoctorResponse;
import org.project.model.response.ServiceResponse;
import org.project.security.AccountDetails;
import org.project.service.AppointmentService;
import org.project.service.DoctorService;
import org.project.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CustomerDashboardController {

    private DoctorService doctorService;

    private ServiceService serviceService;

    private AppointmentService appointmentService;

    @Autowired
    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Autowired
    public void setServiceService(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @Autowired
    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/patient/openCustomerDashboard")
    public String openDashboard(@AuthenticationPrincipal AccountDetails accountDetails, Model model) {
        List<ServiceResponse> serviceResponses = serviceService.getTopServices(3);
        List<DoctorResponse> doctorResponses = doctorService.getTop6Doctors();
        List<AppointmentDashboardCustomerResponse> appointmentDashboardCustomerResponses = appointmentService.get5AppointmentsByUserId(accountDetails.getUserEntity().getId());

        model.addAttribute("serviceResponses", serviceResponses);
        model.addAttribute("doctorResponses", doctorResponses);
        model.addAttribute("appointmentResponses", appointmentDashboardCustomerResponses);

        return "frontend/dashboard-patient";
    }
}

