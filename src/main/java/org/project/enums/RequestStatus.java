package org.project.enums;

public enum RequestStatus {
    pending("Chờ xử lý"),
    received("Đã nhận"),
    processing("Đang xử lý"),
    completed("Đã hoàn thành"),
    rejected("Đã từ chối");

    private final String value;

    RequestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
