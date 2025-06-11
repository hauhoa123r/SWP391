package org.project.api;

import org.project.model.response.TestListResponse;
import org.project.service.TestRequestService;
import org.project.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lab-test")
public class LabTestAPI {
    @Autowired
    TestRequestService testRequestService;
    @Autowired
    TestService testService;

    @PostMapping("/{appoint_id}")
    public ResponseEntity<String> createTestRequest(@PathVariable("appoint_id") Long appoint_id) {
        Long id = testRequestService.createTestRequest(appoint_id);
        return ResponseEntity.ok("Test Request Created Successfully, "+id);
    }

    @GetMapping("/test-list")
    public List<TestListResponse> getAll(){
        return testService.getTestList();
    }
    @GetMapping("/search/")
    public List<TestListResponse> searchTestList(@RequestParam String name){
        return testService.getTestListLikeName(name);
    }
}
