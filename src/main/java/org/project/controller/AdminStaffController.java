package org.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.entity.StaffEntity;
import org.project.enums.StaffStatus;
import org.project.model.request.AdminStaffUpdateRequest;
import org.project.model.response.AdminStaffDetailResponse;
import org.project.model.response.AdminStaffResponse;
import org.project.repository.StaffRepository;
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
    private final StaffRepository staffRepository;

    private static final java.util.List<String> ROLE_NAMES = java.util.List.of(
            org.project.enums.StaffRole.DOCTOR,
            org.project.enums.StaffRole.TECHNICIAN,
            org.project.enums.StaffRole.SCHEDULING_COORDINATOR,
            org.project.enums.StaffRole.PHARMACIST,
            org.project.enums.StaffRole.INVENTORY_MANAGER,
            org.project.enums.StaffRole.LAB_RECEIVER
    ).stream().map(Enum::name).toList();

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
        model.addAttribute("roles", ROLE_NAMES);
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

    // ====== Soft Delete Staff (Following User Pattern) ======
    @PostMapping("/deactivate/{id}")
    public String deactivateStaff(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminStaffService.deactivateStaff(id);
            redirectAttributes.addFlashAttribute("successMessage", "Nhân viên đã được vô hiệu hóa thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi vô hiệu hóa nhân viên: " + e.getMessage());
        }
        return "redirect:/admin/staffs";
    }

    @GetMapping("/deleted")
    public String getDeletedStaffs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<AdminStaffResponse> deletedStaffs = adminStaffService.getDeletedStaffs(pageable);
        
        model.addAttribute("staffPage", deletedStaffs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", deletedStaffs.getTotalPages());
        
        return "admin/staff/deleted";
    }

    @PostMapping("/restore/{id}")
    public String restoreStaff(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminStaffService.restoreStaff(id);
            redirectAttributes.addFlashAttribute("successMessage", "Nhân viên đã được khôi phục thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi khôi phục nhân viên: " + e.getMessage());
        }
        return "redirect:/admin/staffs/deleted";
    }
    @PostMapping("/delete-permanent/{id}")
    public String deleteStaffPermanently(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminStaffService.deleteStaffPermanently(id);
            redirectAttributes.addFlashAttribute("successMessage", "Nhân viên đã bị xóa vĩnh viễn.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi khi xóa vĩnh viễn: " + e.getMessage());
        }
        return "redirect:/admin/staffs/deleted";
    }
    @GetMapping("/dashboard/staffs/deleted")
    public String showDeletedStaffs(Model model,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StaffEntity> deletedStaffs = staffRepository.findByStaffStatus(StaffStatus.INACTIVE, pageable);

        model.addAttribute("deletedStaffs", deletedStaffs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", deletedStaffs.getTotalPages());
        model.addAttribute("totalItems", deletedStaffs.getTotalElements());

        return "dashboard/staff-deleted"; // đúng với vị trí file của bạn
    }



}