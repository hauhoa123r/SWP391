package org.project.enums;

public enum ProductSortType {
    DEFAULT("Mặc định"),
    RATING("Đánh giá"),
    PRICE_ASC("Giá tăng dần"),
    PRICE_DESC("Giá giảm dần");

    private final String type;

    ProductSortType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
