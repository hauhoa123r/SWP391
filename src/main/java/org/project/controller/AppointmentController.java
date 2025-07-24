package org.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppointmentController {

    @GetMapping("/patient/appointment")
    public String getAppointmentPage() {
        return "/frontend/appointment";
    }
}
