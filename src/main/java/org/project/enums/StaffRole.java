package org.project.enums;

import lombok.Getter;

@Getter
public enum StaffRole {
    DOCTOR("doctor"),
    TECHNICIAN("technician"),
    SCHEDULING_COORDINATOR("scheduling_coordinator"),
    PHARMACIST("pharmacist"),
    INVENTORY_MANAGER("inventory_manager"),
    LAB_RECEIVER("lab_receiver");

    private final String value;

    StaffRole(String value) {
        this.value = value;
    }
}
