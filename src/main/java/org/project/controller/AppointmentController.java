package org.project.controller;

import org.project.model.dto.AppointmentDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppointmentController {

    @GetMapping("/patient/appointment")
    public String getAppointmentPage() {
        return "/frontend/appointment";
    }

    @GetMapping("/admin/appointment")
    public String getAppointmentAdminPage(ModelMap modelMap) {
        modelMap.addAttribute("appointmentDTO", new AppointmentDTO());
        return "/dashboard/appointment";
    }
}
