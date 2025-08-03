package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechnicianRank {
    JUNIOR_TECHNICIAN("Kỹ thuật viên cơ bản"),
    TECHNICIAN("Kỹ thuật viên"),
    SENIOR_TECHNICIAN("Kỹ thuật viên cao cấp"),
    LEAD_TECHNICIAN("Kỹ thuật viên trưởng"),
    TECHNICAL_SUPERVISOR("Giám sát kỹ thuật");

    private final String rank;
}
