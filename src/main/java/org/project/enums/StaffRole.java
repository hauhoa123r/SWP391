package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StaffRole {
    DOCTOR("Bác sĩ"),
    TECHNICIAN("Kỹ thuật viên"),
    NURSE("Y tá"),
    SCHEDULING_COORDINATOR("Điều phối lịch"),
    PHARMACIST("Dược sĩ"),
    INVENTORY_MANAGER("Quản lý kho"),
    LAB_RECEIVER("Nhân viên nhận mẫu");

    private final String role;
}
