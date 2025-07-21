package org.project.api;

import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.SampleFilterDTO;
import org.project.model.response.ResultSampleResponse;
import org.project.service.SampleScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/result")
public class ResultSampleAPI
{
    @Autowired
    private SampleScheduleService sampleScheduleServiceImpl;


    @GetMapping("/filter")
    public ResponseEntity<?> filterResult(@ModelAttribute SampleFilterDTO sampleFilterDTO){
        try {
            Page<ResultSampleResponse> results =  sampleScheduleServiceImpl.getAllResultSample(sampleFilterDTO);
            if (results == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid search parameters");
            }
            return ResponseEntity.ok(results);
        }  catch (ResourceNotFoundException | IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
