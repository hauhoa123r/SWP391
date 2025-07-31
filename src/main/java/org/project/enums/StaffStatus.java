package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StaffStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động");

    private final String status;
}
