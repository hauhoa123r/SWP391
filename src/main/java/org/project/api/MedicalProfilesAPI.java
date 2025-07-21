package org.project.api;

import org.project.model.request.UpdateMedicalProfileRequest;
import org.project.model.response.MedicalProfilesResponse;
import org.project.service.MedicalProfilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-profile")
public class MedicalProfilesAPI {
    @Autowired
    private MedicalProfilesService medicalProfileService;

    @GetMapping("/{patient_id}")
    public ResponseEntity<MedicalProfilesResponse> getMedicalProfile(@PathVariable("patient_id") Long patient_id) {
        return ResponseEntity.ok(medicalProfileService.getMedicalProfileOfPatient(patient_id));
    }

    @PutMapping("/{patient_id}")
    public ResponseEntity<MedicalProfilesResponse> updateMedicalProfile(
            @PathVariable("patient_id") Long patientId,
            @RequestBody UpdateMedicalProfileRequest request) {
        MedicalProfilesResponse updated = medicalProfileService.updateAllergiesAndChronicDiseases(
                patientId, request.getAllergies(), request.getChronicDiseases());
        return ResponseEntity.ok(updated);
    }
}
