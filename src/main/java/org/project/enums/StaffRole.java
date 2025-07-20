package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StaffRole {
    DOCTOR("Doctor"),
    TECHNICIAN("Technician"),
    SCHEDULING_COORDINATOR("Scheduling Coordinator"),
    PHARMACIST("Pharmacist"),
    INVENTORY_MANAGER("Inventory Manager");
    private final String value;
}
