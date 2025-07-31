package org.project.enums;

public enum ProductStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động");

    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
