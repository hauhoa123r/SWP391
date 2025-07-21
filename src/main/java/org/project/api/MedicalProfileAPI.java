package org.project.api;

import org.project.model.request.UpdateMedicalProfileRequest;
import org.project.model.response.MedicalProfileResponse;
import org.project.service.MedicalProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-profile")
public class MedicalProfileAPI {
    @Autowired
    private  MedicalProfileService medicalProfileService;

    @GetMapping("/{patient_id}")
    public ResponseEntity<MedicalProfileResponse> getMedicalProfile(@PathVariable("patient_id") Long patient_id) {
        return ResponseEntity.ok(medicalProfileService.getMedicalProfileOfPatient(patient_id));
    }

    @PutMapping("/{patient_id}")
    public ResponseEntity<MedicalProfileResponse> updateMedicalProfile(
            @PathVariable("patient_id") Long patientId,
            @RequestBody UpdateMedicalProfileRequest request) {
        MedicalProfileResponse updated = medicalProfileService.updateAllergiesAndChronicDiseases(
                patientId, request.getAllergies(), request.getChronicDiseases());
        return ResponseEntity.ok(updated);
    }
}
