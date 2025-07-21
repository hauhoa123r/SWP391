package org.project.model.dto;

import lombok.Data;
import org.project.enums.StaffShiftSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class StaffMonthlyScheduleView {
    private Long staffId;
    private String staffName;

    // Ví dụ: 2025-07-17 → [MORNING, NIGHT]
    private Map<LocalDate, List<StaffShiftSlot>> shiftsPerDay;
    
    // Tổng số ngày làm việc trong tháng này
    private int totalWorkingDaysThisMonth;
    private int totalShiftsThisMonth;
    
    // Số lượng ca trực theo từng loại
    private Map<StaffShiftSlot, Integer> shiftTypeCounts;
    
    // Tháng và năm hiển thị
    private int month;
    private int year;
}
