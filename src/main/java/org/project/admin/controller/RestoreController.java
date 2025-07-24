package org.project.admin.controller;

import lombok.RequiredArgsConstructor;
import org.project.admin.service.restore.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/restore")
@RequiredArgsConstructor
public class RestoreController {

    private final UserRestoreService userRestoreService;
    private final PatientRestoreService patientRestoreService;
    private final StaffRestoreService staffRestoreService;
    private final ProductRestoreService productRestoreService;
    private final CouponRestoreService couponRestoreService;

    @PostMapping("/users/{id}")
    public ResponseEntity<?> restoreUser(@PathVariable Long id) {
        userRestoreService.restoreById(id);
        return ResponseEntity.ok("Khôi phục User thành công");
    }

    @PostMapping("/patients/{id}")
    public ResponseEntity<?> restorePatient(@PathVariable Long id) {
        patientRestoreService.restoreById(id);
        return ResponseEntity.ok("Khôi phục Patient thành công");
    }

    @PostMapping("/staffs/{id}")
    public ResponseEntity<?> restoreStaff(@PathVariable Long id) {
        staffRestoreService.restoreById(id);
        return ResponseEntity.ok("Khôi phục Staff thành công");
    }

    @PostMapping("/products/{id}")
    public ResponseEntity<?> restoreProduct(@PathVariable Long id) {
        productRestoreService.restoreById(id);
        return ResponseEntity.ok("Khôi phục Product thành công");
    }

    @PostMapping("/coupons/{id}")
    public ResponseEntity<?> restoreCoupon(@PathVariable Long id) {
        couponRestoreService.restoreById(id);
        return ResponseEntity.ok("Khôi phục Coupon thành công");
    }
}

