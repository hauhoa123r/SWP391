package org.project.admin.dto.request;

import org.project.admin.enums.coupon.CouponStatus;
import org.project.admin.enums.coupon.DiscountType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CouponSearchRequest {
    private String keyword;              // Tìm kiếm theo code hoặc description
    private DiscountType discountType;
    private CouponStatus status;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
    private LocalDate expirationDateFrom;
    private LocalDate expirationDateTo;
    private BigDecimal minOrderAmountFrom;
    private BigDecimal minOrderAmountTo;
}
