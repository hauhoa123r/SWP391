package org.project.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING("pending"),
    FULLFILED("fullfiled"),
    CANCELLED("cancelled");

    private final String value;
}
