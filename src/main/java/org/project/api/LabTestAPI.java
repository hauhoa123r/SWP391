package org.project.api;

import org.project.model.request.TestListRequest;
import org.project.model.response.TestListResponse;
import org.project.model.response.TestRequestResponse;
import org.project.service.TestRequestItemService;
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
    private TestRequestService testRequestService;
    @Autowired
    private TestService testService;
    @Autowired
    private TestRequestItemService testRequestItemService;

    @PostMapping("/{appoint_id}")
    public ResponseEntity<String> createTestRequest(@PathVariable("appoint_id") Long appoint_id,@RequestBody TestListRequest testListRequest ) {
        Long id = testRequestService.createTestRequest(appoint_id);
        testRequestItemService.createTestRequestItem(id,testListRequest.getListTest());
        return ResponseEntity.ok("Test Request Created Successfully, "+id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestRequestResponse> getTestList(@PathVariable Long id){
        return ResponseEntity.ok(testRequestService.getTestRequest(id));
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
