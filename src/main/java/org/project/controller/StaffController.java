package org.project.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.project.entity.StaffEntity;
import org.project.enums.StaffRole;
import org.project.service.StaffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/staffs")
public class StaffController {

    private final StaffService staffService;


    // Show all staff
    @GetMapping
    public String showStaffList(Model model) {
        List<StaffEntity> staffList = staffService.getAllStaff();
        Map<StaffRole, List<StaffEntity>> groupedByRole = staffList.stream()
                .collect(Collectors.groupingBy(StaffEntity::getStaffRole));
        Long staffId = 2l; //hardcode
        boolean alreadyCheckedIn = staffService.hasCheckedInToday(staffId);

        model.addAttribute("alreadyCheckedIn", alreadyCheckedIn);
        model.addAttribute("staffs", groupedByRole); // Add to Thymeleaf model
        return "/frontend/staff-menu"; // Thymeleaf view name (e.g., templates/staff-list.html)
    }

    @PostMapping("/checkin")
    public String processCheckIn(@RequestParam Long staffId,
                                 RedirectAttributes redirectAttributes) {
        staffId = 2l; //hard code id
        boolean success = staffService.processCheckIn(staffId);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Check-in successful for Staff ID: " + staffId);
            return "redirect:/staffs";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Staff ID '" + staffId + "' not found or check-in failed.");
            return "redirect:/staffs";
        }
    }

}
