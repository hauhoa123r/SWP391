package org.project.enums;

/**
 * Enum đại diện cho loại giảm giá của coupon
 */
public enum DiscountType {
    PERCENTAGE("Phần trăm"), // Giảm giá theo phần trăm (%)
    FIXED("Số tiền cố định"); // Giảm giá theo số tiền cố định
    
    private final String displayName;
    
    DiscountType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
} 