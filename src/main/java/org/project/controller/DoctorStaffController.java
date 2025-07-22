package org.project.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;
import org.project.repository.DepartmentRepository;
import org.project.repository.HospitalRepository;
import org.project.service.DoctorStaffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/doctor-staffs")
@RequiredArgsConstructor
public class DoctorStaffController {

    private final DoctorStaffService doctorStaffService;
    private final HospitalRepository hospitalRepo;
    private final DepartmentRepository departmentRepo;

    private static final List<String> ROLE_NAMES = List.of(
            "DOCTOR", "TECHNICIAN", "SCHEDULING_COORDINATOR",
            "PHARMACIST", "INVENTORY_MANAGER", "LAB_RECEIVER");

    private static final List<String> STAFF_TYPES = List.of("FULL_TIME", "PART_TIME_CONTRACT");

    @GetMapping
    public String index() {
        return "redirect:/admin/staffs";
    }

//    @GetMapping("/list")
//    public String list(@RequestParam(value = "q", required = false) String q,
//                       @RequestParam(value = "exclude", required = false) Long excludeId,
//                       Model model) {
//        List<DoctorStaffResponse> staffs = (q == null || q.isBlank())
//                ? doctorStaffService.getAllDoctorStaff()
//                : doctorStaffService.searchDoctorStaff(q);
//        if (excludeId != null) {
//            staffs = staffs.stream().filter(s -> !excludeId.equals(s.getStaffId())).toList();
//        }
//        model.addAttribute("staffs", staffs);
//        model.addAttribute("q", q);
//        return "doctor-staff/list";
//    }

    @GetMapping("/create")
    public String showCreateForm(@RequestParam(value = "reset", defaultValue = "false") boolean reset,
                                 Model model) {
        // Always create new form object if reset=true or no existing staff data
        if (reset || !model.containsAttribute("staff")) {
            model.addAttribute("staff", new DoctorStaffRequest());
        }
        prepareFormModel(model, false);
        return "dashboard/user-add";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("staff") @Valid DoctorStaffRequest request,
                         @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        
        // Kiểm tra Jakarta Bean Validation trước
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.staff", bindingResult);
            redirectAttributes.addFlashAttribute("staff", request);
            return "redirect:/admin/doctor-staffs/create";
        }

        try {
            // Xử lý upload avatar nếu có
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarUrl = doctorStaffService.handleAvatarUpload(avatarFile);
                request.setAvatarUrl(avatarUrl);
            }

            // Gọi service để tạo nhân viên (bao gồm validation)
            DoctorStaffResponse saved = doctorStaffService.createDoctorStaff(request);
            redirectAttributes.addFlashAttribute("success", "Tạo mới nhân viên thành công!");
            return "redirect:/admin/doctor-staffs/detail/" + saved.getStaffId();

        } catch (IOException | IllegalArgumentException ex) {
            // Xử lý lỗi (bao gồm validation errors từ service)
            model.addAttribute("staff", request);
            model.addAttribute("error", ex.getMessage());
            prepareFormModel(model, false);
            return "dashboard/user-add";
        }
    }

    @GetMapping("/detail/{id}")
    public String redirectToView(@PathVariable("id") Long id) {
        return "redirect:/admin/doctor-staffs/detail/" + id + "/view";
    }

    @GetMapping("/detail/{id}/view")
    public String viewDetail(@PathVariable("id") Long id, Model model) {
        DoctorStaffResponse staff = doctorStaffService.getDoctorStaff(id);
        model.addAttribute("staff", staff);
        return "dashboard/user-detail";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            DoctorStaffRequest staffRequest = doctorStaffService.getUpdateForm(id);
            model.addAttribute("staffRequest", staffRequest);
            model.addAttribute("staffId", id);
            prepareFormModel(model, true);
            return "admin/staff/edit";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/doctor-staffs";
        }
    }

    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable("id") Long id) {
        return "redirect:/admin/doctor-staffs/list?exclude=" + id;
    }

    private void prepareFormModel(Model model, boolean isEdit) {
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("hospitals", hospitalRepo.findAll());
        model.addAttribute("departments", departmentRepo.findAll());
        model.addAttribute("roles", ROLE_NAMES);
        model.addAttribute("staffTypes", STAFF_TYPES);
    }
}
