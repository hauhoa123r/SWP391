package org.project.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING("Chờ xử lý"),
    FULLFILED("Đã hoàn thành"),
    CANCELLED("Đã hủy");

    private final String value;
}
