package org.project.api;

import org.project.model.request.SampleRequestDTO;
import org.project.service.TestTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestTypeAPI {
    @Autowired
    private TestTypeService testTypeService;

    @PostMapping("/test/create")
    public ResponseEntity<?> createTestType(@RequestBody SampleRequestDTO request) {
        Boolean isCreate = testTypeService.isCreateTestType(request);
        if(!isCreate){
            return ResponseEntity.badRequest().body("Test type already exist");
        }
        return ResponseEntity.ok("Saved successfully");
    }

    @GetMapping("/test/delete/{id}")
    public ResponseEntity<?> deleteTestType(@PathVariable Long id) {
        Boolean isDelete = testTypeService.isDeleteTestType(id);
        if(!isDelete){
            return ResponseEntity.badRequest().body("Test type not exist");
        }
        return ResponseEntity.ok("Saved successfully");
    }

    @GetMapping("/test/restore/{id}")
    public ResponseEntity<?> restoreTestType(@PathVariable Long id) {
        Boolean isDelete = testTypeService.isRestoreTestType(id);
        if(!isDelete){
            return ResponseEntity.badRequest().body("Test type not exist");
        }
        return ResponseEntity.ok("Saved successfully");
    }
}
