package org.project.api;

import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.ResultTestDTO;
import org.project.model.response.ResultAppointmentResponse;
import org.project.service.ResultSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/result-lab")
public class SendResultAPI {

    @Autowired
    private ResultSampleService resultSampleService;

    @GetMapping("/filter")
    public ResponseEntity<?> filterResult(@ModelAttribute ResultTestDTO resultTestDTO){
        try {
            Page<ResultAppointmentResponse> results = resultSampleService.filterResultSample(resultTestDTO);
            if (results == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid search parameters");
            }
            return ResponseEntity.ok(results);
        }  catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/send/{id}")
    public ResponseEntity<?> approveResult(@PathVariable Long id){
        Boolean isApprove = resultSampleService.approveResultSample(id);
        return ResponseEntity.ok("success");
    }
}
