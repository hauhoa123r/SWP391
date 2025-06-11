package org.project.enums;

public enum StaffRole {
    DOCTOR("Doctor"),
    TECHNICIAN("Technician"),
    SCHEDULING_COORDINATOR("Scheduling Coordinator"),
    PHARMACIST("Pharmacist"),
    INVENTORY_MANAGER("Inventory Manager"),;

    private final String value;
    StaffRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
