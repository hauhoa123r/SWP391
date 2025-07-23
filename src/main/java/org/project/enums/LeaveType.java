package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LeaveType {
    ANNUAL_LEAVE("Annual Leave"),
    SICK_LEAVE("Sick Leave"),
    MATERNITY_LEAVE("Maternity Leave"),
    PATERNITY_LEAVE("Paternity Leave"),
    UNPAID_LEAVE("Unpaid Leave"),
    STUDY_LEAVE("Study Leave"),
    EMERGENCY_LEAVE("Emergency Leave");

    private final String type;
}
