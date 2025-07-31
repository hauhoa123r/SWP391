package org.project.enums;

public enum StaffShiftSlot {
    MORNING("Sáng"),
    AFTERNOON("Chiều"),
    EVENING("Tối"),
    NIGHT("Đêm");

    private final String slot;

    StaffShiftSlot(String slot) {
        this.slot = slot;
    }

    public String getSlot() {
        return slot;
    }
}
