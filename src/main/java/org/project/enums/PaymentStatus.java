package org.project.enums;

public enum PaymentStatus {
    SUCCESS("success"),
    FAILED("failed");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
