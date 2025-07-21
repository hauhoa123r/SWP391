package org.project.api;

import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.AssignmentListDTO;
import org.project.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentAPI {

    @Autowired
    private AssignmentService assignmentService;


    @GetMapping("/search")
    public ResponseEntity<?> getAssignmentBySearch(@ModelAttribute AssignmentListDTO search) {
        try {
            Page<AssignmentListDTO> results = (Page<AssignmentListDTO>) assignmentService.getAssignmentBySearch(search);
            return ResponseEntity.ok(results);
        } catch (IllegalAccessException e) {
            return ResponseEntity.badRequest().body("Invalid search parameters");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/receive/{id}")
    public ResponseEntity<?> receivePatientLabText(@PathVariable Long id){
        boolean isUpdate = assignmentService.receivePatient(id);
        if(!isUpdate){
            ResponseEntity.badRequest().body("Không thành công");
        }
        return ResponseEntity.ok("Thành công");
    }

    @GetMapping("/receives/{ids}")
    public ResponseEntity<?> receivePatientByMultileChoise(@PathVariable List<Long> ids){
        boolean isUpdate = assignmentService.reveicePatientByMultileChoise(ids);
        if(!isUpdate){
            ResponseEntity.badRequest().body("Không thành công");
        }
        return ResponseEntity.ok("Thành công");
    }

}
