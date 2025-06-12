package org.project.api;

import org.project.model.dto.PatientDTO;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class PatientAPI {

    private PatientService patientService;

    @Autowired
    public PatientAPI(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/api/patient")
    public ModelAndView getUserPatientRelationships(@RequestParam Long userId) {
        ModelAndView modelAndView = new ModelAndView("patient_add");

        List<String> relationships = patientService.getAllRelationships(userId);
        if (relationships != null && !relationships.isEmpty()) {
            modelAndView.addObject("relationships", relationships);
        } else {
            modelAndView.addObject("error", "No relationships found.");
        }
        return modelAndView;
    }
}
