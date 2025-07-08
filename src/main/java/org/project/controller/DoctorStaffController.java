// ================================
// 1. DoctorStaffController
// ================================
package org.project.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;
import org.project.repository.DepartmentRepository;
import org.project.repository.HospitalRepository;
import org.project.service.DoctorStaffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/doctor-staffs")
@RequiredArgsConstructor
public class DoctorStaffController {

    private final DoctorStaffService doctorStaffService;
    private final HospitalRepository hospitalRepo;
    private final DepartmentRepository departmentRepo;

    @GetMapping
    public String listDoctorStaff(Model model) {
        List<DoctorStaffResponse> staffs = doctorStaffService.getAllDoctorStaff();
        model.addAttribute("staffs", staffs);
        return "redirect:/admin/doctor-staffs/list";
    }

    @GetMapping("/list")
    public String listDoctorStaffAfterRedirect(@RequestParam(value = "q", required = false) String q,
                                               Model model) {
        List<DoctorStaffResponse> staffs = (q == null || q.isBlank())
                ? doctorStaffService.getAllDoctorStaff()
                : doctorStaffService.searchDoctorStaff(q);
        model.addAttribute("staffs", staffs);
        model.addAttribute("q", q);
        return "dashboard/user-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        // Nếu redirect có flashAttr, giữ nguyên để hiển thị lỗi; nếu F5/đi thẳng thì khởi tạo mới
        if (!model.containsAttribute("staff")) {
            model.addAttribute("staff", new DoctorStaffRequest());
        }
        model.addAttribute("isEdit", false);
        model.addAttribute("hospitals", hospitalRepo.findAll());
        model.addAttribute("departments", departmentRepo.findAll());
        model.addAttribute("roles", List.of("DOCTOR", "NURSE", "TECHNICIAN"));
        model.addAttribute("staffTypes", List.of("FULL_TIME", "PART_TIME"));
        return "dashboard/user-add";
    }

    @PostMapping("/create")
    public String createDoctorStaff(@ModelAttribute("staff") @Valid DoctorStaffRequest request,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // Post/Redirect/Get để khi F5 không còn lỗi đỏ
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.staff", bindingResult);
            redirectAttributes.addFlashAttribute("staff", request);
            return "redirect:/admin/doctor-staffs/create";
        }

        try {
            DoctorStaffResponse saved = doctorStaffService.createDoctorStaff(request);
            redirectAttributes.addFlashAttribute("success", "Tạo mới nhân viên thành công!");
            return "redirect:/admin/doctor-staffs/detail/" + saved.getStaffId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("staff", request);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("hospitals", hospitalRepo.findAll());
            model.addAttribute("departments", departmentRepo.findAll());
            model.addAttribute("roles", List.of("DOCTOR", "NURSE", "TECHNICIAN"));
            model.addAttribute("staffTypes", List.of("FULL_TIME", "PART_TIME"));
            return "dashboard/user-add";
        }
    }

    @GetMapping("/detail/{id}")
    public String viewDoctorStaffDetail(@PathVariable("id") Long id, Model model) {
        DoctorStaffResponse staff = doctorStaffService.getDoctorStaff(id);
        model.addAttribute("staff", staff);
        return "redirect:/admin/doctor-staffs/detail/" + id + "/view";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            DoctorStaffRequest staffRequest = doctorStaffService.getUpdateForm(id);
            model.addAttribute("staffRequest", staffRequest);
            model.addAttribute("staffId", id);
            model.addAttribute("departments", departmentRepo.findAll());
            model.addAttribute("hospitals", hospitalRepo.findAll());
            model.addAttribute("roles", List.of("DOCTOR", "NURSE", "TECHNICIAN"));
            model.addAttribute("staffTypes", List.of("FULL_TIME", "PART_TIME"));
            return "admin/staff/edit";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/doctor-staffs";
        }
    }

    @GetMapping("/detail/{id}/view")
    public String viewDoctorStaffDetailAfterRedirect(@PathVariable("id") Long id, Model model) {
        DoctorStaffResponse staff = doctorStaffService.getDoctorStaff(id);
        model.addAttribute("staff", staff);
        return "dashboard/user-detail";
    }
}
