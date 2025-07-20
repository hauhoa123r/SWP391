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

        model.addAttribute("staffs", groupedByRole); // Add to Thymeleaf model
        return "/frontend/staff-menu"; // Thymeleaf view name (e.g., templates/staff-list.html)
    }
    
	@PostMapping("/checkin")
    public String processCheckIn(@RequestParam Long employeeId,
                                 RedirectAttributes redirectAttributes) {
		employeeId = 2l; //hard code id
        
//		if (employeeId == null) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Employee ID not found.");
//            return "redirect:/checkin";
//        }

        boolean success = staffService.processCheckIn(employeeId);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Check-in successful for Employee ID: " + employeeId);
            return "redirect:/checkin-success";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee ID '" + employeeId + "' not found or check-in failed.");
            return "redirect:/checkin-error";
        }
    }

}
