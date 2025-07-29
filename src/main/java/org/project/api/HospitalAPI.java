package org.project.api;

import jakarta.validation.Valid;
import org.project.model.dto.HospitalDTO;
import org.project.model.response.HospitalResponse;
import org.project.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class HospitalAPI {

    private final int PAGE_SIZE_FOR_BOOKING = 4;
    private final int PAGE_SIZE_FOR_LIST = 6;

    private HospitalService hospitalService;

    @Autowired
    public void setHospitalService(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/api/hospital/page/{index}")
    public ResponseEntity<Map<String, Object>> getAllHospitalsByPage(@PathVariable int index, @ModelAttribute HospitalDTO hospitalDTO) {
        Page<HospitalResponse> hospitalResponsePage = hospitalService.getHospitals(index, PAGE_SIZE_FOR_LIST, hospitalDTO);
        return ResponseEntity.ok(Map.of("hospitals", hospitalResponsePage.getContent(), "totalPages", hospitalResponsePage.getTotalPages(), "currentPage", hospitalResponsePage.getNumber()));
    }

    @GetMapping("/api/admin/hospital/page/{index}")
    public Map<String, Object> getHospitalsForAdmin(@PathVariable int index, @ModelAttribute HospitalDTO hospitalDTO) {
        Page<HospitalResponse> hospitalResponsePage = hospitalService.getHospitals(index, PAGE_SIZE_FOR_LIST, hospitalDTO);
        return Map.of("items", hospitalResponsePage.getContent(), "totalPages", hospitalResponsePage.getTotalPages(), "currentPage", hospitalResponsePage.getNumber());
    }

    @GetMapping("/api/patient/booking/hospital/page/{index}")
    public ResponseEntity<Map<String, Object>> searchHospitalsByPage(@PathVariable int index) {
        Page<HospitalResponse> hospitalResponsePage = hospitalService.getHospitals(index, PAGE_SIZE_FOR_BOOKING);
        return ResponseEntity.ok(Map.of("hospitals", hospitalResponsePage.getContent(), "totalPages", hospitalResponsePage.getTotalPages(), "currentPage", hospitalResponsePage.getNumber()));
    }

    @GetMapping("/api/patient/booking/hospital/page/{index}/search/{keyword}")
    public ResponseEntity<Map<String, Object>> searchHospitalsByPageAndName(@PathVariable int index, @PathVariable String keyword) {
        Page<HospitalResponse> hospitalResponsePage = hospitalService.searchHospitalsByKeyword(index, PAGE_SIZE_FOR_BOOKING, keyword);
        return ResponseEntity.ok(Map.of("hospitals", hospitalResponsePage.getContent(), "totalPages", hospitalResponsePage.getTotalPages(), "currentPage", hospitalResponsePage.getNumber()));
    }

    @PostMapping("/api/admin/hospital")
    public void createHospitalForAdmin(@RequestBody @Valid HospitalDTO hospitalDTO) {
        hospitalService.createHospital(hospitalDTO);
    }

    @PutMapping("/api/admin/hospital/{hospitalId}")
    public void updateHospitalForAdmin(@PathVariable Long hospitalId, @RequestBody @Valid HospitalDTO hospitalDTO) {
        hospitalService.updateHospital(hospitalId, hospitalDTO);
    }

    @DeleteMapping("/api/admin/hospital/{hospitalId}")
    public void deleteHospitalForAdmin(@PathVariable Long hospitalId) {
        hospitalService.deleteHospital(hospitalId);
    }
}
