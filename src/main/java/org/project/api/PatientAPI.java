package org.project.api;

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

    @GetMapping("/api/patient")
    public ModelAndView getUserPatientRelationships(@RequestParam Long userId) {
        ModelAndView modelAndView = new ModelAndView("frontend/patient-add-previous");

        List<String> relationships = patientService.getAllRelationships(userId);
        if (relationships != null && !relationships.isEmpty()) {
            modelAndView.addObject("relationships", relationships);
        } else {
            modelAndView.addObject("error", "No relationships found.");
        }
        return modelAndView;
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
