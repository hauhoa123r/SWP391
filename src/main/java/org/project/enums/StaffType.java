package org.project.enums;

public enum StaffType {
    PART_TIME_CONTRACT("PART_TIME_CONTRACT"),
    TECHNICIAN("TECHNICIAN"),
    SCHEDULING_COORDINATOR("SCHEDULING_COORDINATOR"),
    PHARMACIST("PHARMACIST"),
    INVENTORY_MANAGER("INVENTORY_MANAGER");

    private final String value;

    StaffType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

}
