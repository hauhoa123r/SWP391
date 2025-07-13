package org.project.api;

import org.project.entity.SampleEntity;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.RejectCollectDTO;
import org.project.model.dto.RejectSampleScheduleDTO;
import org.project.model.dto.SampleFilterDTO;
import org.project.model.request.CreateSamplePatientRequest;
import org.project.model.response.SampleConfirmResponse;
import org.project.model.response.SampleScheduleResponse;
import org.project.service.SampleScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sample")
public class SampleAPI {

    @Autowired
    private SampleScheduleService sampleScheduleServiceImpl;

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@ModelAttribute SampleFilterDTO sampleFilterDTO) {
        try {
            Page<SampleScheduleResponse> results =  sampleScheduleServiceImpl.getAllSampleSchedule(sampleFilterDTO);
            if (results == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid search parameters");
            }
            return ResponseEntity.ok(results);
        }  catch (ResourceNotFoundException | IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSample(@ModelAttribute CreateSamplePatientRequest request) {
        boolean isSuccess = sampleScheduleServiceImpl.createSampleSchedule(request);
        if (isSuccess) {
            return ResponseEntity.ok().body("Created successfully");
        } else {
            return ResponseEntity.status(400).body("Creation failed");
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectSample(@RequestBody RejectSampleScheduleDTO rejectSampleScheduleDTO){
        Boolean isSuccess = sampleScheduleServiceImpl.rejectSampleSchedule(rejectSampleScheduleDTO);
        if(isSuccess) {
            return ResponseEntity.ok().body("Rejected successfully");
        }
        return  ResponseEntity.status(400).body("Rejected failed");
    }

    @GetMapping("/confim/filter")
    public ResponseEntity<?> filterConfirm(@ModelAttribute SampleFilterDTO sampleFilterDTO) {
        try {
            Page<SampleConfirmResponse> results =  sampleScheduleServiceImpl.getAllSampleConfirm(sampleFilterDTO);
            if (results == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid search parameters");
            }
            return ResponseEntity.ok(results);
        }  catch (ResourceNotFoundException | IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/approve/{id}")
    public ResponseEntity<?> approveSample(@PathVariable Long id){
        Boolean isSuccess = sampleScheduleServiceImpl.approveSampleSchedule(id);
        if(isSuccess) {
            return ResponseEntity.ok().body("Approved successfully");
        }
        return ResponseEntity.ok("sucess");
    }

    @PostMapping("/reject-collect")
    public ResponseEntity<?> rejectCollect(@RequestBody RejectCollectDTO rejectCollectDTO){
        Boolean isSuccess = sampleScheduleServiceImpl.rejectCollect(rejectCollectDTO);
        if(isSuccess) {
            return ResponseEntity.ok().body("Approved successfully");
        }
        return ResponseEntity.ok("sucess");
    }

    @PostMapping("/reject-cofirm")
    public ResponseEntity<?> rejectSampleComfirm(@RequestBody RejectSampleScheduleDTO rejectSampleScheduleDTO){
        Boolean isSuccess = sampleScheduleServiceImpl.rejectSampleComfirm(rejectSampleScheduleDTO);
        if(isSuccess) {
            return ResponseEntity.ok().body("Rejected successfully");
        }
        return  ResponseEntity.status(400).body("Rejected failed");
    }
}
