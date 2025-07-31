package org.project.api;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.project.entity.UserEntity;
import org.project.model.dto.PatientDTO;
import org.project.model.response.PatientResponse;
import org.project.security.AccountDetails;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PatientAPI {
    private final int PAGE_SIZE = 2;

    private PatientService patientService;

    @Autowired
    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/api/patient/booking/patient/page/{pageIndex}")
    public ResponseEntity<Map<String, Object>> getAllPatients(@AuthenticationPrincipal AccountDetails accountDetails, @PathVariable int pageIndex, HttpSession httpSession) {
        UserEntity userEntity = accountDetails.getUserEntity();
        Page<PatientResponse> patientResponsePage = patientService.getPatientsByUser(userEntity.getId(), pageIndex, PAGE_SIZE);
        return ResponseEntity.ok(
                Map.of(
                        "patients", patientResponsePage.getContent(),
                        "currentPage", patientResponsePage.getNumber(),
                        "totalPages", patientResponsePage.getTotalPages()
                )
        );
    }

    @GetMapping("/api/patient/booking/patient/page/{pageIndex}/search/{keyword}")
    public ResponseEntity<Map<String, Object>> getPatientsByUserAndKeyword(
            @AuthenticationPrincipal AccountDetails accountDetails,
            @PathVariable int pageIndex,
            @PathVariable String keyword) {
        UserEntity userEntity = accountDetails.getUserEntity();
        Page<PatientResponse> patientResponsePage = patientService.getPatientsByUserAndKeyword(userEntity.getId(), keyword, pageIndex, PAGE_SIZE);
        return ResponseEntity.ok(
                Map.of(
                        "patients", patientResponsePage.getContent(),
                        "currentPage", patientResponsePage.getNumber(),
                        "totalPages", patientResponsePage.getTotalPages()
                )
        );
    }

    @PatchMapping("/api/patient/update/{patientId}")
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

    @DeleteMapping("/api/patient/delete/{patientId}")
    public ResponseEntity<?> deletePatient(@PathVariable Long patientId) {
        try {
            patientService.deletePatient(patientId);
            return ResponseEntity.ok("Patient deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/api/admin/patient/page/{pageIndex}")
    public Map<String, Object> getAllPatientsForAdmin(@PathVariable int pageIndex, @ModelAttribute PatientDTO patientDTO) {
        Page<PatientResponse> patientResponsePage = patientService.getPatients(pageIndex, 6, patientDTO);
        return Map.of(
                "items", patientResponsePage.getContent(),
                "currentPage", patientResponsePage.getNumber(),
                "totalPages", patientResponsePage.getTotalPages()
        );
    }

    @PostMapping("/api/admin/patient")
    public void addPatientForAdmin(@RequestBody @Valid PatientDTO patientDTO) {
        patientService.createPatientAndUser(patientDTO);
    }

    @PutMapping("/api/admin/patient/{patientId}")
    public void updatePatientForAdmin(@PathVariable Long patientId, @RequestBody @Valid PatientDTO patientDTO) {
        patientService.updatePatient(patientId, patientDTO);
    }

    @DeleteMapping("/api/admin/patient/{patientId}")
    public void deletePatientForAdmin(@PathVariable Long patientId) {
        patientService.deletePatient(patientId);
    }
}
