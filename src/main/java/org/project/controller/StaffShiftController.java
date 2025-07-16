package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.enums.StaffShiftSlot;
import org.project.model.dto.StaffWeeklyScheduleView;
import org.project.model.request.AssignShiftRequest;
import org.project.service.StaffShiftService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/staff-shifts")
@RequiredArgsConstructor
public class StaffShiftController {

    private final StaffShiftService staffShiftService;

    // ✅ Hiển thị lịch ca hôm nay cho tất cả nhân viên
    @GetMapping
    public String viewAllShiftsToday(Model model) {
        model.addAttribute("shifts", staffShiftService.getTodayShiftOverview());
        return "dashboard/assign-staff-shifts";
    }

    // ✅ Hiển thị lịch làm việc theo tuần cho 1 nhân viên
    @GetMapping("/weekly/{staffId}")
    public String viewWeeklySchedule(@PathVariable Long staffId,
                                     @RequestParam(defaultValue = "0") int week,
                                     Model model) {
        StaffWeeklyScheduleView schedule = staffShiftService.getWeeklySchedule(staffId, week);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
        Map<String, List<StaffShiftSlot>> shiftsMap = new LinkedHashMap<>();
        List<String> dateKeys = new ArrayList<>();

        schedule.getShiftsPerDay().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String dateKey = entry.getKey().format(formatter);
                    shiftsMap.put(dateKey, entry.getValue());
                    dateKeys.add(dateKey);
                });

        model.addAttribute("schedule", schedule);
        model.addAttribute("dates", dateKeys);         // Danh sách String yyyy-MM-dd
        model.addAttribute("shiftsMap", shiftsMap);    // Map<String, List<ShiftSlot>>
        return "dashboard/view-weekly-schedule";
    }

    // ✅ Gán ca trực mới
    @PostMapping("/assign")
    public String assignShift(@ModelAttribute AssignShiftRequest request) {
        staffShiftService.assignShift(request);
        return "redirect:/admin/staff-shifts";
    }

    // ✅ Xoá ca trực
    @PostMapping("/delete/{shiftId}")
    public String deleteShift(@PathVariable Long shiftId) {
        staffShiftService.deleteShift(shiftId);
        return "redirect:/admin/staff-shifts";
    }
}
