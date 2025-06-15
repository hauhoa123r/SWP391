package org.project.controller;

import org.project.model.response.PatientResponse;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class PatientController {

    private PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patient/showAddPrevious")
    public String showAddPatientForm() {
        return "/frontend/patient-add-previous"; // This should return the name of the HTML template for adding a patient
    }

    @GetMapping("/patient/showList")
    public String showPatientList(@RequestParam Long userId, Model model) {

        List<PatientResponse> patients = patientService.getAllPatientsByUserId(userId);
        if (patients == null || patients.isEmpty()) {
            model.addAttribute("errorMessage", "No patients found for the given user ID.");
        } else {
            model.addAttribute("patients", patients);
        }
        return "/frontend/patient-list"; // This should return the name of the HTML template for showing the patient list
    }


}
