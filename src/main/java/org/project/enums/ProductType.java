package org.project.enums;

public enum ProductType {
    PRICING_PLAN("Gói dịch vụ"),
    SERVICE("Dịch vụ"),
    TEST("Xét nghiệm"),
    MEDICINE("Thuốc"),
    MEDICAL_PRODUCT("Sản phẩm y tế");

    private final String value;

    ProductType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
