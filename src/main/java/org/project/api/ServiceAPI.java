package org.project.api;

import org.project.model.response.ServiceResponse;
import org.project.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/service")
public class ServiceAPI {
    private final int PAGE_SIZE_FOR_APPOINTMENT = 6;
    private final int PAGE_SIZE_FOR_LIST = 9;

    private ServiceService serviceService;

    @Autowired
    public void setProductService(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping("/page/{pageIndex}/department/{departmentId}")
    public ResponseEntity<Map<String, Object>> getAllServicesByPageAndStaffId(@PathVariable int pageIndex, @PathVariable Long departmentId) {
        Page<ServiceResponse> serviceResponsePage = serviceService.getServicesByDepartment(departmentId, pageIndex, PAGE_SIZE_FOR_APPOINTMENT);
        return ResponseEntity.ok(
                Map.of(
                        "services", serviceResponsePage.getContent(),
                        "totalPages", serviceResponsePage.getTotalPages(),
                        "currentPage", serviceResponsePage.getNumber()
                )
        );
    }

    @GetMapping("/page/{pageIndex}/department/{departmentId}/search/{keyword}")
    public ResponseEntity<Map<String, Object>> searchServicesByStaffIdAndName(
            @PathVariable int pageIndex,
            @PathVariable Long departmentId,
            @PathVariable String keyword) {
        Page<ServiceResponse> serviceResponsePage = serviceService.searchServicesByDepartmentAndKeyword(departmentId, keyword, pageIndex, PAGE_SIZE_FOR_APPOINTMENT);
        return ResponseEntity.ok(
                Map.of(
                        "services", serviceResponsePage.getContent(),
                        "totalPages", serviceResponsePage.getTotalPages(),
                        "currentPage", serviceResponsePage.getNumber()
                )
        );
    }

    @GetMapping("/page/{pageIndex}")
    public ResponseEntity<Map<String, Object>> service(@PathVariable int pageIndex) {
        Page<ServiceResponse> productRespsonsePage = serviceService.getServices(pageIndex, PAGE_SIZE_FOR_LIST);
        return ResponseEntity.ok(
                Map.of(
                        "services", productRespsonsePage.getContent(),
                        "currentPage", productRespsonsePage.getNumber(),
                        "totalPages", productRespsonsePage.getTotalPages()
                )
        );
    }
}
