package org.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PatientController {

    @GetMapping("/patient-add")
    public String showAddPatientForm() {
        return "/dashboard/patient_add"; // This should return the name of the HTML template for adding a patient
    }
}
