package org.project.controller;

import org.project.entity.StaffEntity;
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
	
	
	@GetMapping("/checkin")
	public String showCheckInForm(Model model) {
		return "frontend/staff-menu";
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

    @GetMapping("/checkin-success")
    public String checkInSuccess() {
        return "checkin-success";
    }

    @GetMapping("/checkin-error")
    public String checkInError() {
        return "checkin-error";
    }
}
