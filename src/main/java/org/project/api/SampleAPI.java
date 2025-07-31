package org.project.api;

import org.project.entity.ResultEntity;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.FilterSampleNameDTO;
import org.project.model.dto.RejectCollectDTO;
import org.project.model.dto.RejectSampleScheduleDTO;
import org.project.model.dto.SampleFilterDTO;
import org.project.model.request.CreateSamplePatientRequest;
import org.project.model.response.SampleConfirmResponse;
import org.project.model.response.SampleFilterResponse;
import org.project.model.response.SampleScheduleResponse;
import org.project.model.response.SymtomResponse;
import org.project.service.ReferrenceRangeService;
import org.project.service.ResultSampleService;
import org.project.service.SampleScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/sample")
public class SampleAPI {

    @Autowired
    private SampleScheduleService sampleScheduleServiceImpl;

    @Autowired
    private ResultSampleService resultSampleServiceImpl;

    private final ReferrenceRangeService referrenceRangeService;

    SampleAPI(ReferrenceRangeService referrenceRangeService){
        this.referrenceRangeService = referrenceRangeService;
    }

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
        return ResponseEntity.ok("success");
    }

    @PostMapping("/reject-cofirm")
    public ResponseEntity<?> rejectSampleComfirm(@RequestBody RejectSampleScheduleDTO rejectSampleScheduleDTO){
        Boolean isSuccess = sampleScheduleServiceImpl.rejectSampleComfirm(rejectSampleScheduleDTO);
        if(isSuccess) {
            return ResponseEntity.ok().body("Rejected successfully");
        }
        return  ResponseEntity.status(400).body("Rejected failed");
    }

    @PostMapping("/result")
    public ResponseEntity<?> getResult(@RequestBody Map<String, String> dataUnit){
        SymtomResponse symtomResponse = referrenceRangeService.getSymtomResponse(dataUnit);
        return ResponseEntity.ok(symtomResponse);
    }

    @PostMapping("/set-result")
    public ResponseEntity<?> setResultSample(@RequestBody Map<String, String> dataDTO){
        ResultEntity isCheck = resultSampleServiceImpl.isSaveResultSample(dataDTO);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/filter/name")
    public ResponseEntity<?> filterSampleName(@ModelAttribute FilterSampleNameDTO filterSampleNameDTO){
        try {
            Page<SampleFilterResponse> results = sampleScheduleServiceImpl.searchSampleSchedule(filterSampleNameDTO);
            if (results == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid search parameters");
            }
            return ResponseEntity.ok(results);
        }  catch (ResourceNotFoundException | IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/filter/inactive")
    public ResponseEntity<?> filterSampleInactive(@ModelAttribute FilterSampleNameDTO filterSampleNameDTO){
        try {
            Page<SampleFilterResponse> results = sampleScheduleServiceImpl.searchSampleInactive(filterSampleNameDTO);
            if (results == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid search parameters");
            }
            return ResponseEntity.ok(results);
        }  catch (ResourceNotFoundException | IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
