package org.project.model.dto;

import lombok.Data;
import org.project.enums.StaffShiftSlot;

import java.util.Map;

@Data
public class StaffShiftViewModel {
    private Long staffId;
    private String fullName;
    private String departmentName;
    private String hospitalName;
    private int totalWorkingDaysThisMonth;

    // Ví dụ: "MORNING" -> "Working", "AFTERNOON" -> "Off"
    private Map<StaffShiftSlot, String> statusPerSlotToday;
}
