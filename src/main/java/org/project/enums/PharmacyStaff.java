package org.project.enums;

public enum PharmacyStaff {
    PHARMACIST("Dược sĩ"),
    CASHIRER("Thu ngân"),
    INVENTORY_MANAGER("Quản lý kho"),
    DELIVERY("Giao hàng");

    private final String value;

    PharmacyStaff(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
