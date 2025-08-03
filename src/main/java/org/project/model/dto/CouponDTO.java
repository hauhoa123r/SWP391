package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.CouponStatus;
import org.project.enums.DiscountType;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {
    private Long id;
    private String code;
    private String description;
    private BigDecimal value;
    private BigDecimal minimumOrderAmount;
    private Date expirationDate;
    private DiscountType discountType;
    private CouponStatus couponStatus;
    
    // Các trường bổ sung cho hiển thị trên UI
    private boolean isValid; // Đánh dấu coupon còn hạn hay không
    private int usageCount; // Số lần đã sử dụng
    
    // Phương thức để kiểm tra coupon còn hạn hay không
    public boolean isExpired() {
        if (expirationDate == null) {
            return false;
        }
        Date today = new Date(System.currentTimeMillis());
        return expirationDate.before(today);
    }
} 