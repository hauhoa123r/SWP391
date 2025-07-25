package org.project.api;

import org.project.model.request.SampleRequestDTO;
import org.project.model.response.TestTypeListResponse;
import org.project.service.TestTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestTypeAPI {
    @Autowired
    private TestTypeService testTypeService;

    @GetMapping("/test-types")
    public ResponseEntity<Page<TestTypeListResponse>> getListTest(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TestTypeListResponse> result;

        if (StringUtils.hasText(search)) {
            result = testTypeService.searchTestTypes(search, pageable);
        } else {
            result = testTypeService.getAllTestTypes(pageable);
        }
        return ResponseEntity.ok(result);
    }
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
