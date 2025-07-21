package org.project.api;

import org.project.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/medical-record")
public class MedicalRecordVAPI {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @GetMapping("/{appointment_id}/main-reason")
    public ResponseEntity<String> getMainReason(@PathVariable Long appointment_id) {
        return ResponseEntity.ok(medicalRecordService.getMainReason(appointment_id));
    }
    @GetMapping("/{appointment_id}/diagnosis")
    public ResponseEntity<String> getDiagnosis(@PathVariable Long appointment_id) {
        return ResponseEntity.ok(medicalRecordService.getDiagnosis(appointment_id));
    }
    @GetMapping("/{appointment_id}/plan")
    public ResponseEntity<String> getTreatmentPlan(@PathVariable Long appointment_id) {
        return ResponseEntity.ok(medicalRecordService.getPlan(appointment_id));
    }

    @PostMapping("/{appointment_id}/diagnosis")
    public ResponseEntity<Boolean> addDiagnosis(@PathVariable Long appointment_id,@RequestBody Map<String, String> body) {
        String diagnosis = body.get("diagnosis");
        return ResponseEntity.ok(medicalRecordService.addDiagnosis(appointment_id,diagnosis));
    }
    @PostMapping("/{appointment_id}/treatment-plan")
    public ResponseEntity<Boolean> addTreatmentPlan(@PathVariable Long appointment_id,@RequestBody Map<String, String> body) {
        String treatmentPlan = body.get("treatment_plan");
        return ResponseEntity.ok(medicalRecordService.addPlan(appointment_id,treatmentPlan));
    }
    @PostMapping("/{appointment_id}/main-reason")
    public ResponseEntity<Boolean> addMainReason(@PathVariable Long appointment_id,@RequestBody Map<String, String> body) {
        String mainReason = body.get("main-reason");
        return ResponseEntity.ok(medicalRecordService.addPlan(appointment_id,mainReason));
    }
}
