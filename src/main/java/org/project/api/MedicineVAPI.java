package org.project.api;

import org.project.model.response.MedicineListVResponse;
import org.project.service.ProductVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class MedicineVAPI {
    @Autowired
    private ProductVService productVService;
    @GetMapping("/medicines")
    public Page<MedicineListVResponse> getMedicines(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productVService.getMedicineList(keyword, pageable);
    }
}
