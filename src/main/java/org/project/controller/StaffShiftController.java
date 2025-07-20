package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.StaffEntity;
import org.project.entity.StaffShiftEntity;
import org.project.enums.StaffShiftSlot;
import org.project.model.dto.StaffMonthlyScheduleView;
import org.project.model.dto.StaffShiftViewModel;
import org.project.model.request.AssignShiftRequest;
import org.project.repository.StaffRepository;

import org.project.service.StaffShiftService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

import java.util.*;

@Controller
@RequestMapping("/admin/staff-shifts")
@RequiredArgsConstructor
public class StaffShiftController {

    private final StaffShiftService staffShiftService;
    private final StaffRepository staffRepository;

    // ✅ 1. Tổng quan phân ca trong ngày (có phân trang + lọc theo ngày)
    @GetMapping
    public String dailyShiftOverview(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "id") String sortBy,
                                     @RequestParam(defaultValue = "asc") String sortDir,
                                     @RequestParam(required = false) String date,
                                     Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        LocalDate targetDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        Page<StaffShiftViewModel> staffShifts = staffShiftService.getDailyShiftOverviewPaginated(targetDate, pageable);

        model.addAttribute("staffShifts", staffShifts);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("selectedDate", targetDate.toString());
        model.addAttribute("totalPages", staffShifts.getTotalPages());
        model.addAttribute("totalElements", staffShifts.getTotalElements());
        model.addAttribute("hasNext", staffShifts.hasNext());
        model.addAttribute("hasPrevious", staffShifts.hasPrevious());
        model.addAttribute("isFirst", staffShifts.isFirst());
        model.addAttribute("isLast", staffShifts.isLast());

        // Dữ liệu modal gán ca
        model.addAttribute("allStaff", staffRepository.findAll());
        model.addAttribute("shiftSlots", StaffShiftSlot.values());

        return "dashboard/assign-staff-shifts";
    }

    // ✅ 2. Hiển thị lịch làm việc theo tháng cho 1 nhân viên
    @GetMapping("/monthly/{staffId}")
    public String viewMonthlySchedule(@PathVariable Long staffId,
                                      @RequestParam(defaultValue = "0") int month,
                                      Model model) {

        staffShiftService.ensureMonthlyShiftsGenerated(staffId, month);
        StaffMonthlyScheduleView schedule = staffShiftService.getMonthlySchedule(staffId, month);

        // Dữ liệu cho Thymeleaf
        Map<String, List<StaffShiftSlot>> shiftsMap = staffShiftService.getShiftsMap(schedule);
        List<String> dateKeys = new ArrayList<>(shiftsMap.keySet());

        // Sắp xếp ngày để lịch luôn hiển thị đúng thứ tự
        Collections.sort(dateKeys);

        model.addAttribute("schedule", schedule);
        model.addAttribute("dates", dateKeys);
        model.addAttribute("shiftsMap", shiftsMap);

        return "dashboard/view-monthly-schedule";
    }

    // ✅ 3a. Hiển thị trang gán ca trực
    @GetMapping("/assign")
    public String showAssignShiftPage(@RequestParam(required = false) String date,
                                      @RequestParam(required = false) Long hospitalId,
                                      @RequestParam(required = false) Long departmentId,
                                      Model model) {
        LocalDate selectedDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();

        // Load all hospitals for selection
        model.addAttribute("allHospitals", staffShiftService.getAllHospitals());
        
        // Load all departments initially (not filtered by hospital)
        // This ensures departments are always available for selection
        List<org.project.entity.DepartmentEntity> allDepartments = new ArrayList<>();
        try {
            // Get all departments from all hospitals
            for (org.project.entity.HospitalEntity hospital : staffShiftService.getAllHospitals()) {
                allDepartments.addAll(staffShiftService.getDepartmentsByHospital(hospital.getId()));
            }
        } catch (Exception e) {
            // Fallback: empty list if there's an error
            allDepartments = java.util.Collections.emptyList();
        }
        model.addAttribute("allDepartments", allDepartments);
        
        // Load all staff initially (not filtered)
        // This ensures staff are always available for selection
        List<org.project.entity.StaffEntity> allStaff = new ArrayList<>();
        try {
            allStaff = staffRepository.findAll();
        } catch (Exception e) {
            // Fallback: empty list if there's an error
            allStaff = java.util.Collections.emptyList();
        }
        model.addAttribute("allStaff", allStaff);

        // Set selected values if provided
        if (hospitalId != null) {
            model.addAttribute("selectedHospitalId", hospitalId);
        }
        if (departmentId != null) {
            model.addAttribute("selectedDepartmentId", departmentId);
        }

        model.addAttribute("shiftSlots", StaffShiftSlot.values());
        model.addAttribute("selectedDate", selectedDate.toString());
        return "dashboard/assign-shift-page";
    }

    // ✅ 3b. Xử lý gán ca trực
    @PostMapping("/assign")
    public String assignShift(@ModelAttribute AssignShiftRequest request,
                              @RequestParam(required = false) String date,
                              RedirectAttributes redirectAttributes) {
        try {
            staffShiftService.assignShift(request);

            StaffEntity staff = staffRepository.findById(request.getStaffId()).orElse(null);
            String staffName = (staff != null) ? staff.getFullName() : "Nhân viên";
            String shiftName = getShiftDisplayName(request.getShiftSlot());

            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Đã gán thành công ca " + shiftName + " cho " + staffName + " vào ngày " + request.getDate());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Lỗi khi gán ca trực: " + e.getMessage());
        }

        String redirectUrl = "redirect:/admin/staff-shifts";
        if (date != null && !date.isEmpty()) {
            redirectUrl += "?date=" + date;
        }
        return redirectUrl;
    }

    private String getShiftDisplayName(StaffShiftSlot slot) {
        return switch (slot) {
            case MORNING -> "Sáng (6:00-12:00)";
            case AFTERNOON -> "Chiều (12:00-18:00)";
            case EVENING -> "Tối (18:00-24:00)";
            case NIGHT -> "Đêm (24:00-6:00)";
            default -> slot.toString();
        };
    }

    // ✅ 4. Xoá ca trực
    @PostMapping("/delete/{shiftId}")
    public String deleteShift(@PathVariable Long shiftId) {
        staffShiftService.deleteShift(shiftId);
        return "redirect:/admin/staff-shifts";
    }

    // ✅ 5. Xem chi tiết các ca trực trong tháng (dạng bảng, có phân trang)
    @GetMapping("/monthly-details")
    public String viewMonthlyShiftDetails(@RequestParam(defaultValue = "0") int month,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "date") String sortBy,
                                          @RequestParam(defaultValue = "desc") String sortDir,
                                          Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<StaffShiftEntity> monthlyShifts = staffShiftService.getMonthlyShiftDetails(month, pageable);
        LocalDate targetMonth = LocalDate.now().withDayOfMonth(1).plusMonths(month);

        model.addAttribute("monthlyShifts", monthlyShifts);
        model.addAttribute("currentMonth", month);
        model.addAttribute("monthName", targetMonth.getMonth().getDisplayName(
                java.time.format.TextStyle.FULL, Locale.forLanguageTag("vi-VN")));
        model.addAttribute("year", targetMonth.getYear());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "dashboard/monthly-shift-details";
    }

    // ✅ 6. Sinh dữ liệu mẫu
    @GetMapping("/test-data/{staffId}")
    public String addTestData(@PathVariable Long staffId) {
        staffShiftService.generateTestShiftsForCurrentMonth(staffId);
        return "redirect:/admin/staff-shifts/monthly/" + staffId;
    }
}
