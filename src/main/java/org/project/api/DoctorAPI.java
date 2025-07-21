package org.project.api;

import org.project.enums.StaffRole;
import org.project.model.response.DoctorResponse;
import org.project.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorAPI {
    private final StaffRole DOCTOR_ROLE = StaffRole.DOCTOR;
    private final int PAGE_SIZE = 4;

    private DoctorService doctorService;

    @Autowired
    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/page/{pageIndex}/hospital/{hospitalId}/department/{departmentId}")
    public ResponseEntity<Map<String, Object>> getDoctorsByHospitalAndDepartment(@PathVariable int pageIndex, @PathVariable Long hospitalId, @PathVariable Long departmentId) {
        Page<DoctorResponse> doctorResponsePage = doctorService.getAllByHospitalAndDepartment(hospitalId, departmentId, pageIndex, PAGE_SIZE);
        return ResponseEntity.ok(
                Map.of(
                        "doctors", doctorResponsePage.getContent(),
                        "totalPages", doctorResponsePage.getTotalPages(),
                        "currentPage", doctorResponsePage.getNumber()
                )
        );
    }

    @GetMapping("/page/{pageIndex}/hospital/{hospitalId}/department/{departmentId}/search/{keyword}")
    public ResponseEntity<Map<String, Object>> searchDoctorsByHospitalAndDepartmentAndKeyword(
            @PathVariable int pageIndex,
            @PathVariable Long hospitalId,
            @PathVariable Long departmentId,
            @PathVariable String keyword) {
        Page<DoctorResponse> doctorResponsePage = doctorService.searchAllByHospitalAndDepartmentAndKeyword(hospitalId, departmentId, keyword, pageIndex, PAGE_SIZE);
        return ResponseEntity.ok(
                Map.of(
                        "doctors", doctorResponsePage.getContent(),
                        "totalPages", doctorResponsePage.getTotalPages(),
                        "currentPage", doctorResponsePage.getNumber()
                )
        );
    }
}
