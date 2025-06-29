package org.project.api;

import org.project.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medical-record")
public class MedicalRecordAPI {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @GetMapping("/{appointment_id}")
    public ResponseEntity<String> getMainReason(@PathVariable Long appointment_id) {
        return ResponseEntity.ok(medicalRecordService.getMainReason(appointment_id));
    }
}
