package org.project.api;

import org.project.model.dto.MedicalProfileDTO;
import org.project.service.MedicalProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MedicalProfileAPI {

    private MedicalProfileService medicalProfileService;

    @Autowired
    public MedicalProfileAPI(MedicalProfileService medicalProfileService) {
        this.medicalProfileService = medicalProfileService;
    }

    @PostMapping("/api/profile")
    public void createMedicalProfile(@RequestBody MedicalProfileDTO medicalProfileDTO) {
        medicalProfileService.createMedicalProfile(medicalProfileDTO);
    }

}
