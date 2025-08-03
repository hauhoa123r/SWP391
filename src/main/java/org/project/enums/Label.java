package org.project.enums;

public enum Label {
    NEW("Mới"),
    SALE("Khuyến mãi"),
    STANDARD("Tiêu chuẩn");

    private final String value;

    Label(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
