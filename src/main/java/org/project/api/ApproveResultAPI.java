package org.project.api;

import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.ApproveResultDTO;
import org.project.model.response.ApproveResultFilterResponse;
import org.project.service.ApproveResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/approve-result")
public class ApproveResultAPI {

    @Autowired
    private ApproveResultService approveResultService;

    @GetMapping("/filter")
    public ResponseEntity<?> filterResult(@ModelAttribute ApproveResultDTO approveResultDTO){
        try {
            Page<ApproveResultFilterResponse> results =  approveResultService.getAllApproveResult(approveResultDTO);
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
}
