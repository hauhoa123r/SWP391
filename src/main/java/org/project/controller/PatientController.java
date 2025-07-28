package org.project.controller;

import org.project.model.dto.PatientDTO;
import org.project.model.response.PatientResponse;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patient/showAddPrevious")
    public String showAddPatientForm() {
        return "/frontend/patient-add-previous"; // This should return the name of the HTML template for adding a patient
    }

    @GetMapping("/patient/showList/{userId}")
    public String showPatientList(@PathVariable Long userId,
                                  @RequestParam(defaultValue = "0") int pageIndex,
                                  Model model) {

        Page<PatientResponse> patientResponsePage = patientService.getPatientsByUser(userId, pageIndex, 6);
        List<PatientResponse> patients = patientResponsePage.getContent();

        for (PatientResponse patientResponse : patients) {
            if (patientResponse.getAvatarUrl() != null && !patientResponse.getAvatarUrl().isEmpty()) {
                String base64Image = patientService.toConvertFileToBase64(patientResponse.getAvatarUrl());
                patientResponse.setAvatarUrl(base64Image);
            } else {
                patientResponse.setAvatarUrl(null); // Set to null if no avatar URL is present
            }
        }
        model.addAttribute("patients", patients);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("totalPages", patientResponsePage.getTotalPages());
        return "/frontend/patient-list"; // This should return the name of the HTML template for showing the patient list
    }

    @GetMapping("/admin/patient")
    public String showPatientsForAdmin(ModelMap modelMap) {
        modelMap.put("patientDTO", new PatientDTO());
        return "/dashboard/patient";
    }
}
