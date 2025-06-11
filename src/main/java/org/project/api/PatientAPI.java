package org.project.api;

import org.project.model.dto.PatientDTO;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class PatientAPI {

    private PatientService patientService;

    @Autowired
    public PatientAPI(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/api/patient")
    public ModelAndView createPatient(@RequestBody PatientDTO patientDTO) {
        ModelAndView modelAndView = new ModelAndView("redirect:/patient_infor");
        patientService.createPatient(patientDTO);
        modelAndView.addObject("message", "Patient created successfully");
        return modelAndView;
    }
}
