package org.project.api;

import org.project.model.dto.PatientDTO;
import org.project.model.response.PatientResponse;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
public class PatientAPI {
    private final int PAGE_SIZE = 2;

    private PatientService patientService;

    @Autowired
    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/page/{pageIndex}/user/{userId}")
    public ResponseEntity<Map<String, Object>> getAllPatients(@PathVariable int pageIndex, @PathVariable Long userId) {
        Page<PatientResponse> patientResponsePage = patientService.getPatientsByUser(userId, pageIndex, PAGE_SIZE);
        return ResponseEntity.ok(
                Map.of(
                        "patients", patientResponsePage.getContent(),
                        "currentPage", patientResponsePage.getNumber(),
                        "totalPages", patientResponsePage.getTotalPages()
                )
        );
    }

    @GetMapping("/page/{pageIndex}/user/{userId}/search/{keyword}")
    public ResponseEntity<Map<String, Object>> getPatientsByUserAndKeyword(
            @PathVariable int pageIndex,
            @PathVariable Long userId,
            @PathVariable String keyword) {
        Page<PatientResponse> patientResponsePage = patientService.getPatientsByUserAndKeyword(userId, keyword, pageIndex, PAGE_SIZE);
        return ResponseEntity.ok(
                Map.of(
                        "patients", patientResponsePage.getContent(),
                        "currentPage", patientResponsePage.getNumber(),
                        "totalPages", patientResponsePage.getTotalPages()
                )
        );
    }

    @PatchMapping("/update/{patientId}")
    public ResponseEntity<?> updatePatient(@RequestBody PatientDTO patientDTO,
                                           @PathVariable Long patientId) {
        try {
            PatientResponse updatedPatient = patientService.updatePatient(patientId, patientDTO);
            if (updatedPatient != null) {
                return ResponseEntity.ok(updatedPatient);
            } else {
                return ResponseEntity.badRequest().body("Failed to update patient.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{patientId}")
    public ResponseEntity<?> deletePatient(@PathVariable Long patientId) {
        try {
            patientService.deletePatient(patientId);
            return ResponseEntity.ok("Patient deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}
