package org.project.service.impl;


import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import org.project.entity.CouponEntity;
import org.project.enums.DiscountType;
import org.project.exception.CouponException;
import org.project.repository.CouponRepository;
import org.project.service.CartService;
import org.project.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CartService cartService;

    private BigDecimal calculateDiscountedTotal(BigDecimal cartTotal, CouponEntity coupon) {
        BigDecimal discountedTotal;

        if (coupon.getDiscountType() == DiscountType.FIXED) {
            discountedTotal = cartTotal.subtract(coupon.getValue());
        } else if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            BigDecimal percent = coupon.getValue().divide(BigDecimal.valueOf(100));
            discountedTotal = cartTotal.subtract(cartTotal.multiply(percent));
        } else {
            discountedTotal = cartTotal;
        }

        if (discountedTotal.compareTo(BigDecimal.ZERO) < 0) {
            discountedTotal = BigDecimal.ZERO;
        }

        return discountedTotal;
    }

    @Override
    public BigDecimal applyCoupon(String code, Long userId, HttpSession session) throws CouponException {
        Optional<CouponEntity> optionalCoupon = couponRepository.findByCode(code.trim());
        if (optionalCoupon.isEmpty()) {
            throw new CouponException("Coupon code not found. Existing coupon (if any) is still applied.");
        }

        CouponEntity coupon = optionalCoupon.get();

        if (coupon.getExpirationDate().before(new Date())) {
            throw new CouponException("Coupon has expired.");
        }

        BigDecimal cartTotal = cartService.calculateTotal(userId);

        if (coupon.getMinimumOrderAmount() != null && cartTotal.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new CouponException("Order total does not meet the minimum amount.");
        }

        BigDecimal discountedTotal= calculateDiscountedTotal(cartTotal,coupon);


        session.setAttribute("appliedCoupon", coupon);
        session.setAttribute("discountedTotal", discountedTotal);

        return discountedTotal;
    }

}