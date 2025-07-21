package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechnicianRank {
    JUNIOR_TECHNICIAN("JUNIOR_TECHNICIAN"),
    TECHNICIAN("TECHNICIAN"),
    SENIOR_TECHNICIAN("SENIOR_TECHNICIAN"),
    LEAD_TECHNICIAN("LEAD_TECHNICIAN"),
    TECHNICAL_SUPERVISOR("TECHNICAL_SUPERVISOR");

    private final String rank;
}
