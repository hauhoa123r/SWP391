package org.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/doctor")
public class AppointmentController {
    @GetMapping("/home-page")
    public String doctorHomePage(Model model) {
        model.addAttribute("content","/frontend/doctor/doctor_homepage");
        return "frontend/doctor/doctor_layout";
    }
    @GetMapping("/appointments")
    public String doctorAppointments(Model model) {
        model.addAttribute("content", "/frontend/doctor/doctor_appointments");
        return "frontend/doctor/doctor_layout";
    }
    @GetMapping("/in-progress")
    public String doctorInProgress(Model model, @RequestParam("id") Long id) {
        model.addAttribute("content", "/frontend/doctor/appointment_progress");
        model.addAttribute("id", id);
        return "frontend/doctor/doctor_layout";
    }
    @GetMapping("/test-result")
    public String doctorLabTestResult(Model model) {
        model.addAttribute("content","/frontend/doctor/doctor_result");
        return "frontend/doctor/doctor_layout";
    }
}
