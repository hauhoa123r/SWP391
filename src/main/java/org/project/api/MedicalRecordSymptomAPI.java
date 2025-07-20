package org.project.api;

import org.project.model.request.MedicalRecordSymptomRequest;
import org.project.model.response.MedicalRecordSymptomResponse;
import org.project.service.MedicalRecordSymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-record-symptom")
public class MedicalRecordSymptomAPI {

    @Autowired
    private MedicalRecordSymptomService medicalRecordSymptomService;

    @GetMapping("/{medicalRecordId}")
    public ResponseEntity<List<MedicalRecordSymptomResponse>> getSymptomByMedicalRecordId(@PathVariable Long medicalRecordId){
        return ResponseEntity.ok(medicalRecordSymptomService.getSymptoms(medicalRecordId));
    }
    @PostMapping("/{medicalRecordId}")
    public ResponseEntity<List<Long>> addSymptoms(@PathVariable Long medicalRecordId, @RequestBody List<MedicalRecordSymptomRequest> symptomIds) {
        return ResponseEntity.ok(medicalRecordSymptomService.addMedicalRecordSymptom(medicalRecordId,symptomIds));
    }
    @PutMapping("/{symptomId}")
    public ResponseEntity<String> updateSymptoms(@PathVariable Long symptomId, @RequestBody MedicalRecordSymptomRequest symptomRequest) {
        medicalRecordSymptomService.updateMedicalRecordSymptom(symptomId,symptomRequest);
        return ResponseEntity.ok("Symptom Updated");
    }
    @DeleteMapping("/{symptomId}")
    public ResponseEntity<String> deleteSymptoms(@PathVariable Long symptomId) {
        medicalRecordSymptomService.deleteMedicalRecordSymptom(symptomId);
        return ResponseEntity.ok("Symptom Deleted");
    }
}
