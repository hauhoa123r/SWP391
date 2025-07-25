package org.project.controller;

import jakarta.servlet.http.HttpSession;
import org.project.model.response.DoctorHeaderResponse;
import org.project.service.DoctorVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/staff/doctor")
public class AppointmentVController {
    @Autowired
    private DoctorVService doctorService;

    @GetMapping
    public ResponseEntity<DoctorHeaderResponse> getDoctorId(){
        return ResponseEntity.ok(doctorService.getDoctorByUserId(8L));
    }

    @GetMapping("/home-page")
    public String doctorHomePage(Model model, HttpSession session) {
        Long userId = 8L;
        DoctorHeaderResponse doctorResponse = doctorService.getDoctorByUserId(userId);
        model.addAttribute("doctor", doctorResponse);
        model.addAttribute("content","doctor-ui/doctor_homepage");
        return "doctor-ui/doctor_layout";
    }
    @GetMapping("/appointments")
    public String doctorAppointments(Model model) {
        Long userId = 8L;
        DoctorHeaderResponse doctorResponse = doctorService.getDoctorByUserId(userId);
        model.addAttribute("doctor", doctorResponse);
        model.addAttribute("content", "doctor-ui/doctor_appointments");
        return "doctor-ui/doctor_layout";
    }
    @GetMapping("/in-progress")
    public String doctorInProgress(Model model, @RequestParam("id") Long id) {
        Long userId = 8L;
        DoctorHeaderResponse doctorResponse = doctorService.getDoctorByUserId(userId);
        model.addAttribute("doctor", doctorResponse);
        model.addAttribute("content", "doctor-ui/appointment_progress");
        model.addAttribute("id", id);
        return "doctor-ui/doctor_layout";
    }
    @GetMapping("/test-result")
    public String doctorLabTestResult(Model model) {
        model.addAttribute("content","doctor-ui/doctor_result");
        return "doctor-ui/doctor_layout";
    }
}
