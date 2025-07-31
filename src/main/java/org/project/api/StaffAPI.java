package org.project.api;

import jakarta.validation.Valid;
import org.project.model.dto.AvailabilityRequestDTO;
import org.project.model.dto.StaffDTO;
import org.project.model.response.StaffResponse;
import org.project.model.response.StaffSubstituteResponse;
import org.project.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class StaffAPI {
    private StaffService staffService;

    @Autowired
    public void setStaffService(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/api/admin/staff/page/{pageIndex}")
    public Map<String, Object> getStaffsForAdmin(@PathVariable int pageIndex, @ModelAttribute StaffDTO staffDTO) {
        Page<StaffResponse> staffResponsePage = staffService.getStaffs(pageIndex, 6, staffDTO);
        return Map.of(
                "items", staffResponsePage.getContent(),
                "currentPage", staffResponsePage.getNumber(),
                "totalPages", staffResponsePage.getTotalPages()
        );
    }

    @PostMapping("/api/admin/staff")
    public void createStaff(@RequestBody @Valid StaffDTO staffDTO) {
        staffService.createStaff(staffDTO);
    }

    @PutMapping("/api/admin/staff/{staffId}")
    public void updateStaff(@PathVariable Long staffId, @RequestBody @Valid StaffDTO staffDTO) {
        staffService.updateStaff(staffId, staffDTO);
    }

    @PutMapping("/api/admin/staff/upgrade/{staffId}")
    public void setStaffToManager(@PathVariable Long staffId) {
        staffService.setStaffToManager(staffId);
    }


    @DeleteMapping("/api/admin/staff/{staffId}")
    public void deleteStaff(@PathVariable Long staffId) {
        staffService.deleteStaff(staffId);
    }

    @PostMapping("/api/staff/substitutes")
    public ResponseEntity<List<StaffSubstituteResponse>> getAllStaffSubstitutes(@RequestBody AvailabilityRequestDTO availability) {
        List<StaffSubstituteResponse> response = staffService.getAllStaffSubstitutes(availability);
        return ResponseEntity.ok(response);
    }
}
