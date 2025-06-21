package org.project.api;

import org.project.model.response.PatientResponse;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
