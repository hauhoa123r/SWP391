package org.project.api;

import org.project.model.response.TestTypeListResponse;
import org.project.service.TestTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestTypeAPI {
    @Autowired
    private TestTypeService testTypeService;

    @GetMapping("/test-types")
    public ResponseEntity<Page<TestTypeListResponse>> getListTest(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TestTypeListResponse> result;

        if (StringUtils.hasText(search)) {
            result = testTypeService.searchTestTypes(search, pageable);
        } else {
            result = testTypeService.getAllTestTypes(pageable);
        }
        return ResponseEntity.ok(result);
    }
}
