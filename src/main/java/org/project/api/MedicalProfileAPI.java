package org.project.api;

import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.MedicalProfileDTO;
import org.project.model.response.MedicalProfileResponse;
import org.project.service.MedicalProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MedicalProfileAPI {

    private MedicalProfileService medicalProfileService;

    @Autowired
    public MedicalProfileAPI(MedicalProfileService medicalProfileService) {
        this.medicalProfileService = medicalProfileService;
    }

    @PostMapping("/api/profile")
    public ModelAndView createMedicalProfile(@RequestBody MedicalProfileDTO medicalProfileDTO) {
        ModelAndView modelAndView = new ModelAndView("redirect:/patient_infor");
        medicalProfileService.createMedicalProfile(medicalProfileDTO);
        MedicalProfileResponse medicalProfileResponse = medicalProfileService.getMedicalProfileByPatientId(medicalProfileDTO.getPatientId());
        if (medicalProfileResponse == null) {
            throw new ResourceNotFoundException("Medical profile not found for patient with id: " + medicalProfileDTO.getPatientId());
        } else {
            modelAndView.addObject("medicalProfile", medicalProfileResponse);
        }
        return modelAndView;
    }



}
