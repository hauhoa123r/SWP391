package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LeaveType {
    ANNUAL_LEAVE("Nghỉ phép năm"),
    SICK_LEAVE("Nghỉ ốm"),
    MATERNITY_LEAVE("Nghỉ thai sản"),
    PATERNITY_LEAVE("Nghỉ phép cha"),
    UNPAID_LEAVE("Nghĩ không lương"),
    STUDY_LEAVE("Nghỉ để đi học"),
    EMERGENCY_LEAVE("Nghỉ phép khẩn cấp");

    private final String type;
}
