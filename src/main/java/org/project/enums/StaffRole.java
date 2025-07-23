package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StaffRole {
    DOCTOR("doctor"),
    TECHNICIAN("technician"),
    NURSE("nurse"),
    SCHEDULING_COORDINATOR("scheduling_coordinator"),
    PHARMACIST("pharmacist"),
    INVENTORY_MANAGER("inventory_manager"),
    LAB_RECEIVER("lab_receiver");
    private final String value;
}
