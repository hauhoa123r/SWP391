package org.project.enums.operation;

public enum AggregationFunction {
    NONE("Không có"),
    AVG("Trung bình"),
    SUM("Tổng"),
    COUNT("Đếm"),
    MAX("Lớn nhất"),
    MIN("Nhỏ nhất");

    private final String function;

    AggregationFunction(String function) {
        this.function = function;
    }

    public String getFunction() {
        return function;
    }
}
