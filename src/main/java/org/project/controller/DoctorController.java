package org.project.controller;

import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @GetMapping()
    public String showDoctorDashBoard(Model model) {
        model.addAttribute("content","doctor/doctor_homepage");
        return "doctor/doctor_layout";
    }

    @GetMapping("/appointments")
    public String showAppointmentsDashBoard(Model model) {
        model.addAttribute("content","doctor/doctor_appointments");
        return "doctor/doctor_layout";
    }
    @GetMapping("in-progress")
    public String showAppointmentProgressDashBoard(Model model, @RequestParam("appoint_id") Long appointId) {
        model.addAttribute("content","doctor/appointment_progress");
        model.addAttribute("appointment_id",appointId);
        return "doctor/doctor_layout";
    }
}
