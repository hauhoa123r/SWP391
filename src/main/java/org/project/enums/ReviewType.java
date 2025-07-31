package org.project.enums;

/**
 * Enum định nghĩa các loại đánh giá
 */
public enum ReviewType {
    STAFF("Nhân viên"),
    PRODUCT("Sản phẩm"),
    SERVICE("Dịch vụ");

    private final String type;

    ReviewType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}