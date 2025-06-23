package org.project.api;

import org.project.model.response.HospitalResponse;
import org.project.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/hospital")
public class HospitalAPI {

    private final int PAGE_SIZE = 4;

    private HospitalService hospitalService;

    @Autowired
    public void setHospitalService(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @RequestMapping("/page/{index}")
    public ResponseEntity<Map<String, Object>> getAllHospitalsByPage(@PathVariable int index) {
        Page<HospitalResponse> hospitalResponsePage = hospitalService.getHospitals(index, PAGE_SIZE);
        return ResponseEntity.ok(
                Map.of(
                        "hospitals", hospitalResponsePage.getContent(),
                        "totalPages", hospitalResponsePage.getTotalPages(),
                        "currentPage", hospitalResponsePage.getNumber()
                )
        );
    }

    @RequestMapping("/page/{index}/search/{keyword}")
    public ResponseEntity<Map<String, Object>> searchHospitalsByPageAndName(@PathVariable int index, @PathVariable String keyword) {
        Page<HospitalResponse> hospitalResponsePage = hospitalService.searchHospitalsByKeyword(index, PAGE_SIZE, keyword);
        return ResponseEntity.ok(
                Map.of(
                        "hospitals", hospitalResponsePage.getContent(),
                        "totalPages", hospitalResponsePage.getTotalPages(),
                        "currentPage", hospitalResponsePage.getNumber()
                )
        );
    }
}
