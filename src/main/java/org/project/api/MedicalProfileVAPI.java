package org.project.api;

import org.project.model.request.UpdateMedicalProfileRequest;
import org.project.model.response.MedicalProfileVResponse;
import org.project.service.MedicalProfilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-profile")
public class MedicalProfileVAPI {
    @Autowired
    private MedicalProfilesService medicalProfileService;

    @GetMapping("/{patient_id}")
    public ResponseEntity<MedicalProfileVResponse> getMedicalProfile(@PathVariable("patient_id") Long patient_id) {
        return ResponseEntity.ok(medicalProfileService.getMedicalProfileOfPatient(patient_id));
    }

    @PutMapping("/{patient_id}")
    public ResponseEntity<MedicalProfileVResponse> updateMedicalProfile(
            @PathVariable("patient_id") Long patientId,
            @RequestBody UpdateMedicalProfileRequest request) {
        MedicalProfileVResponse updated = medicalProfileService.updateAllergiesAndChronicDiseases(
                patientId, request.getAllergies(), request.getChronicDiseases());
        return ResponseEntity.ok(updated);
    }
}
