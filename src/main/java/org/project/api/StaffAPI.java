package org.project.api;

import org.project.model.dto.AvailabilityRequestDTO;
import org.project.model.response.StaffSubstituteResponse;
import org.project.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/staff")
public class StaffAPI {

    private StaffService staffService;

    @Autowired
    public void setStaffService(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping("/substitutes")
    public ResponseEntity<List<StaffSubstituteResponse>> getAllStaffSubstitutes(@RequestBody AvailabilityRequestDTO availability) {
        List<StaffSubstituteResponse> response = staffService.getAllStaffSubstitutes(availability);
        return ResponseEntity.ok(response);
    }
}
