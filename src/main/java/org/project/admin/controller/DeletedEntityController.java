package org.project.admin.controller;

import lombok.RequiredArgsConstructor;
import org.project.admin.entity.*;
import org.project.admin.service.restore.RestoreQueryService;
import org.project.admin.util.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/deleted")
@RequiredArgsConstructor
public class DeletedEntityController {

    private final RestoreQueryService restoreQueryService;

    @GetMapping("/users")
    public PageResponse<User> getDeletedUsers(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = restoreQueryService.getDeletedUsers(pageable);
        return new PageResponse<>(usersPage);
    }

    @GetMapping("/patients")
    public PageResponse<Patient> getDeletedPatients(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Patient> patientsPage = restoreQueryService.getDeletedPatients(pageable);
        return new PageResponse<>(patientsPage);
    }

    @GetMapping("/staffs")
    public PageResponse<Staff> getDeletedStaffs(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Staff> staffsPage = restoreQueryService.getDeletedStaffs(pageable);
        return new PageResponse<>(staffsPage);
    }

    @GetMapping("/products")
    public PageResponse<Product> getDeletedProducts(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage = restoreQueryService.getDeletedProducts(pageable);
        return new PageResponse<>(productsPage);
    }

    @GetMapping("/coupons")
    public PageResponse<Coupon> getDeletedCoupons(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Coupon> couponsPage = restoreQueryService.getDeletedCoupons(pageable);
        return new PageResponse<>(couponsPage);
    }

    @GetMapping("/all")
    public Map<String, PageResponse<?>> getAll(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Map<String, Page<?>> deletedEntities = restoreQueryService.getAllDeletedEntities(pageable);

        Map<String, PageResponse<?>> response = new HashMap<>();
        deletedEntities.forEach((key, value) -> {
            response.put(key, new PageResponse<>(value));
        });
        return response;
    }
}
