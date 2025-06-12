package org.project.api;

import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.MedicalProfileDTO;
import org.project.model.dto.PatientDTO;
import org.project.model.response.MedicalProfileResponse;
import org.project.model.response.PatientResponse;
import org.project.service.MedicalProfileService;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MedicalProfileAPI {

    private MedicalProfileService medicalProfileService;

    private PatientService patientService;

    @Autowired
    public MedicalProfileAPI(MedicalProfileService medicalProfileService, PatientService patientService) {
        this.medicalProfileService = medicalProfileService;
        this.patientService = patientService;
    }

    @PostMapping("/api/profile")
    public ModelAndView createPatientAndMedicalProfile(@RequestBody MedicalProfileDTO medicalProfileDTO, @RequestBody PatientDTO patientDTO) {
        ModelAndView modelAndView = new ModelAndView("redirect:/patient_infor");

        try {
            PatientResponse patientResponse =  medicalProfileService.addPatientAndMedicalProfile(medicalProfileDTO, patientDTO);
            if (patientResponse != null) {
                modelAndView.addObject("patient", patientResponse);
            } else {
                modelAndView.addObject("error", "Failed to create patient and medical profile.");
            }
        } catch (ResourceNotFoundException e) {
            modelAndView.addObject("error", "Resource not found: " + e.getMessage());
        }
        return modelAndView;
    }

}
