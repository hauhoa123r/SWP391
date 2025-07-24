package org.project.admin.service.restore;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.admin.entity.Coupon;
import org.project.admin.repository.CouponRepository;
import org.project.admin.service.Log.CouponLogService;
import org.project.admin.util.RestoreService;
import org.project.admin.enums.AuditAction;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CouponRestoreService implements RestoreService<Coupon> {

    private final CouponRepository couponRepository;
    private final CouponLogService couponLogService;

    @Override
    @Transactional
    public void restoreById(Long id) {
        Coupon coupon = couponRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Coupon"));

        coupon.setDeleted(false);

        couponRepository.save(coupon);

        couponLogService.logCouponAction(coupon, AuditAction.RESTORE);
    }
}
