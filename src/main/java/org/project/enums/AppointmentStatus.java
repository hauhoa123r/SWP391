package org.project.enums;

import lombok.Getter;

@Getter

public enum AppointmentStatus {
    PENDING("Chờ duyệt"),
    CONFIRMED("Đã xác nhận"),
    CANCELLED("Đã hủy"),
    IN_PROGRESS("Đang khám"),
    COMPLETED("Đã hoàn thành"),
    CONFLICTED("Xung đột lịch");

    private final String status;

    AppointmentStatus(String status) {
        this.status = status;
    }
}
