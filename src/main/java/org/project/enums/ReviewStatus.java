package org.project.enums;

/**
 * Enum đại diện cho các trạng thái của đánh giá sản phẩm
 */
public enum ReviewStatus {
    PENDING("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    HIDDEN("Đã ẩn");

    private final String displayName;

    ReviewStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 