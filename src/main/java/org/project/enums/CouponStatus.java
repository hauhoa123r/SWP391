package org.project.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum CouponStatus {
    ACTIVE("Đang Kích Hoạt"),
    INACTIVE("Ngừng Hoạt Động");

    private final String status;

    CouponStatus(String s) {
        this.status = s;
    }

}
