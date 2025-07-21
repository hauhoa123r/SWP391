package org.project.api;

import org.project.model.dto.DepartmentDTO;
import org.project.model.response.DepartmentResponse;
import org.project.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class DepartmentAPI {

    private final int PAGE_SIZE = 6;
    private DepartmentService departmentService;

    @Autowired
    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/api/department/page/{pageIndex}/hospital/{hospitalId}")
    public ResponseEntity<Map<String, Object>> getAllByPageAndHospital(@PathVariable int pageIndex, @PathVariable Long hospitalId) {
        Page<DepartmentResponse> departmentResponsePage = departmentService.getDepartmentsHaveDoctorByHospital(hospitalId, pageIndex, PAGE_SIZE);
        return ResponseEntity.ok(
                Map.of(
                        "departments", departmentResponsePage.getContent(),
                        "totalPages", departmentResponsePage.getTotalPages(),
                        "currentPage", departmentResponsePage.getNumber()
                )
        );
    }

    @GetMapping("/api/department/page/{pageIndex}/hospital/{hospitalId}/search/{keyword}")
    public ResponseEntity<Map<String, Object>> getAllByPageAndHospitalAndKeyword(
            @PathVariable int pageIndex,
            @PathVariable Long hospitalId,
            @PathVariable String keyword) {
        Page<DepartmentResponse> departmentResponsePage = departmentService.getDepartmentsHaveDoctorByHospitalAndKeyword(hospitalId, keyword, pageIndex, PAGE_SIZE);
        return ResponseEntity.ok(
                Map.of(
                        "departments", departmentResponsePage.getContent(),
                        "totalPages", departmentResponsePage.getTotalPages(),
                        "currentPage", departmentResponsePage.getNumber()
                )
        );
    }

    @GetMapping("/api/department")
    public ResponseEntity<List<DepartmentResponse>> getAll() {
        List<DepartmentResponse> departmentResponses = departmentService.getDepartmentsHaveDoctor();
        return ResponseEntity.ok(departmentResponses);
    }

    @GetMapping("/api/department/page/{pageIndex}")
    public Map<String, Object> getAllByPage(@PathVariable int pageIndex, @ModelAttribute DepartmentDTO departmentDTO) {
        Page<DepartmentResponse> departmentResponsePage = departmentService.getDepartments(pageIndex, PAGE_SIZE, departmentDTO);
        return Map.of(
                "departments", departmentResponsePage.getContent(),
                "totalPages", departmentResponsePage.getTotalPages(),
                "currentPage", departmentResponsePage.getNumber()
        );
    }
}
