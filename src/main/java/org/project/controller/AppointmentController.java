package org.project.controller;

import jakarta.servlet.http.HttpSession;
import org.project.model.response.DoctorResponse;
import org.project.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/staff/doctor")
public class AppointmentController {
    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity<DoctorResponse> getDoctorId(){
        return ResponseEntity.ok(doctorService.getDoctorByUserId(9l));
    }

    @GetMapping("/home-page")
    public String doctorHomePage(Model model, HttpSession session) {
        Long userId = 1L;
        DoctorResponse doctorResponse = doctorService.getDoctorByUserId(userId);
        model.addAttribute("doctor", doctorResponse);
        model.addAttribute("content","/frontend/doctorv2/doctor_homepage");
        return "frontend/doctorv2/doctor_layout";
    }
    @GetMapping("/appointments")
    public String doctorAppointments(Model model) {
        Long userId = 1L;
        DoctorResponse doctorResponse = doctorService.getDoctorByUserId(userId);
        model.addAttribute("doctor", doctorResponse);
        model.addAttribute("content", "/frontend/doctorv2/doctor_appointments");
        return "frontend/doctorv2/doctor_layout";
    }
    @GetMapping("/in-progress")
    public String doctorInProgress(Model model, @RequestParam("id") Long id) {
        Long userId = 1L;
        DoctorResponse doctorResponse = doctorService.getDoctorByUserId(userId);
        model.addAttribute("doctor", doctorResponse);
        model.addAttribute("content", "/frontend/doctorv2/appointment_progress");
        model.addAttribute("id", id);
        return "frontend/doctorv2/doctor_layout";
    }
    @GetMapping("/test-result")
    public String doctorLabTestResult(Model model) {
        model.addAttribute("content","/frontend/doctor/doctor_result");
        return "frontend/doctor/doctor_layout";
    }
}
