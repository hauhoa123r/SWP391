package org.project.controller;

import jakarta.servlet.http.HttpSession;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    private PatientService patientService;

    @Autowired
    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public String getAppointmentPage(HttpSession session) {
        session.setAttribute("user", patientService.getUserHasPatient());
        return "/frontend/appointment";
    }
}
