package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;
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

    // Hiển thị danh sách toàn bộ nhân sự
    @GetMapping
    public String listDoctorStaff(Model model) {
        List<DoctorStaffResponse> staffs = doctorStaffService.getAllDoctorStaff();
        model.addAttribute("staffs", staffs);
        return "dashboard/user-list";
    }

    // Hiển thị form tạo mới nhân sự
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("staff", new DoctorStaffRequest());
        model.addAttribute("isEdit", false);
        return "dashboard/user-add";
    }

    // Xử lý submit tạo mới nhân sự
    @PostMapping("/create")
    public String createDoctorStaff(@ModelAttribute("staff") DoctorStaffRequest request,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            DoctorStaffResponse saved = doctorStaffService.createDoctorStaff(request);
            redirectAttributes.addFlashAttribute("success", "Tạo mới nhân viên thành công!");
            return "redirect:/admin/doctor-staffs"; // Quay lại danh sách sau khi tạo
        } catch (IllegalArgumentException ex) {
            model.addAttribute("staff", request);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("isEdit", false);
            return "dashboard/user-add";
        }
    }
}
