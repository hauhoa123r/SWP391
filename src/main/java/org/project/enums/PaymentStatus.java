package org.project.enums;

public enum PaymentStatus {
    SUCCESS("Thành công"),
    FAILED("Thất bại");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
