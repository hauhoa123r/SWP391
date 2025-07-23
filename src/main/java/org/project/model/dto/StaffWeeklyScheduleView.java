package org.project.model.dto;

import lombok.Data;
import org.project.enums.StaffShiftSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class StaffWeeklyScheduleView {
    private Long staffId;
    private String staffName;

    // Ví dụ: 2025-07-17 → [MORNING, NIGHT]
    private Map<LocalDate, List<StaffShiftSlot>> shiftsPerDay;
    
    // Tổng số ngày làm việc trong tuần này
    private int totalWorkingDaysThisWeek;
}
