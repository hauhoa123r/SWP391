package org.project.admin.service.restore;

import lombok.RequiredArgsConstructor;
import org.project.admin.entity.*;
import org.project.admin.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestoreQueryService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;

    public Page<User> getDeletedUsers(Pageable pageable) {
        return userRepository.findAllDeleted(pageable);
    }

    public Page<Patient> getDeletedPatients(Pageable pageable) {
        return patientRepository.findAllDeleted(pageable);
    }

    public Page<Staff> getDeletedStaffs(Pageable pageable) {
        return staffRepository.findAllDeleted(pageable);
    }

    public Page<Product> getDeletedProducts(Pageable pageable) {
        return productRepository.findAllDeleted(pageable);
    }

    public Page<Coupon> getDeletedCoupons(Pageable pageable) {
        return couponRepository.findAllDeleted(pageable);
    }

    public Map<String, Page<?>> getAllDeletedEntities(Pageable pageable) {
        return Map.of(
                "users", getDeletedUsers(pageable),
                "patients", getDeletedPatients(pageable),
                "staffs", getDeletedStaffs(pageable),
                "products", getDeletedProducts(pageable),
                "coupons", getDeletedCoupons(pageable)
        );
    }

}
