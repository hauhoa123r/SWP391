package org.project.api;

import org.project.model.request.TestListRequest;
import org.project.model.response.TestRequestInAppointment;
import org.project.service.TestRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestRequestAPI {
    @Autowired
    private TestRequestService testRequestService;

    @GetMapping("/test-request/{appointmentId}")
    public ResponseEntity<List<TestRequestInAppointment>> getTestRequests(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(testRequestService.getListTestRequest(appointmentId));
    }

    @PostMapping("/test-request/add")
    public ResponseEntity<Boolean> addTestRequest(@RequestBody TestListRequest testListRequest) {
        return ResponseEntity.ok(testRequestService.addListTestRequestByAppointmentId(testListRequest));
    }

    @DeleteMapping("/test-request/{testRequestId}/delete")
    public ResponseEntity<Boolean> deleteTestRequest(@PathVariable Long testRequestId) {
        return ResponseEntity.ok(testRequestService.deleteTestRequest(testRequestId));
    }
}
