package org.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.model.request.AdminStaffUpdateRequest;
import org.project.model.response.AdminStaffDetailResponse;
import org.project.model.response.AdminStaffResponse;
import org.project.service.AdminStaffService;
import org.project.service.DepartmentService;
import org.project.service.HospitalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/staffs")
@RequiredArgsConstructor
public class AdminStaffController {

    private final AdminStaffService adminStaffService;
    private final DepartmentService departmentService;
    private final HospitalService hospitalService;

    @GetMapping
    public String getStaffList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "name") String field,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<AdminStaffResponse> staffPage;
        switch (field) {
            case "fullName" -> staffPage = adminStaffService.searchByFullName(keyword, pageable);
            case "email" -> staffPage = adminStaffService.searchByEmail(keyword, pageable);
            case "phoneNumber" -> staffPage = adminStaffService.searchByPhoneNumber(keyword, pageable);
            default -> staffPage = adminStaffService.getAllStaffs(pageable, field, keyword);
        }

        model.addAttribute("staffPage", staffPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("field", field);
        model.addAttribute("totalPages", staffPage.getTotalPages());

        return "dashboard/staff";
    }
    @GetMapping("/detail/{id}")
    public String viewStaffDetail(@PathVariable Long id, Model model) {
        AdminStaffDetailResponse staffDetail = adminStaffService.getStaffDetail(id);
        model.addAttribute("staff", staffDetail);
        return "admin/staff/detail";
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("staffId", id);
        model.addAttribute("staffRequest", adminStaffService.getUpdateForm(id));
        model.addAttribute("departments", departmentService.getAll());
        model.addAttribute("hospitals", hospitalService.getHospitals(0, Integer.MAX_VALUE).getContent());
                return "admin/staff/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateStaff(@PathVariable Long id,
                              @ModelAttribute("staffRequest") @Valid AdminStaffUpdateRequest request,
                              BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAll());
            model.addAttribute("hospitals", hospitalService.getHospitals(0, Integer.MAX_VALUE).getContent());
                        return "admin/staff/edit";
        }
        adminStaffService.updateStaff(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công");
        return "redirect:/admin/staffs/detail/" + id;
    }

    // ====== Search Staffs ======
    @GetMapping("/search")
    public String searchStaffs(@RequestParam String field,
                               @RequestParam String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<AdminStaffResponse> staffPage;
        switch (field) {
            case "fullName" -> staffPage = adminStaffService.searchByFullName(keyword, pageable);
            case "email" -> staffPage = adminStaffService.searchByEmail(keyword, pageable);
            case "phoneNumber" -> staffPage = adminStaffService.searchByPhoneNumber(keyword, pageable);
            default -> staffPage = adminStaffService.getAllStaffs(pageable, field, keyword);
        }
        model.addAttribute("staffPage", staffPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("field", field);
        return "dashboard/staff-search";
    }
}