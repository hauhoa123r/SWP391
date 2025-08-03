package org.project.api;

import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.MedicalProfileDTO;
import org.project.model.dto.PatientAndMedicalProfileDTO;
import org.project.model.dto.PatientDTO;
import org.project.model.response.PatientResponse;
import org.project.security.AccountDetails;
import org.project.service.MedicalProfileService;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> createPatientAndMedicalProfile(@RequestBody PatientAndMedicalProfileDTO dto, @AuthenticationPrincipal AccountDetails accountDetails) {
        try {
            PatientDTO patientDTO = dto.getPatientDTO();
            MedicalProfileDTO medicalProfileDTO = dto.getMedicalProfileDTO();
            patientDTO.setUserId(accountDetails.getUserEntity().getId());
            PatientResponse patientResponse = medicalProfileService.addPatientAndMedicalProfile(medicalProfileDTO, patientDTO);
            if (patientResponse != null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Created susccessfully!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to create patient and medical profile.");
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Resource not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create patient and medical profile.");
        }
    }

}
