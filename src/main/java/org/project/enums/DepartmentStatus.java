package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DepartmentStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Ngừng hoạt động");

    private final String status;
}
