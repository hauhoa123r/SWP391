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
import org.project.repository.StaffRepository;
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

    private final DoctorStaffService doctorStaffService;// Service xử lý logic nghiệp vụ liên quan đến bác sĩ (lấy danh sách, tạo mới, sửa, xóa...)
    private final HospitalRepository hospitalRepo;// Repository giao tiếp với bảng Hospital trong CSDL (để lấy danh sách bệnh viện)
    private final StaffRepository staffRepo;// Repository thao tác với bảng Staff, dùng để lấy thông tin nhân viên hoặc role (quyền)
    private final DepartmentRepository departmentRepo;// Repository để truy vấn bảng Department (phòng ban)

    private static final java.util.List<String> ROLE_NAMES = java.util.Arrays.stream(org.project.enums.StaffRole.values())
            .map(Enum::name)
            .toList();
    

    @GetMapping
    public String listDoctorStaff(Model model) {// Hàm xử lý request, tham số `Model` dùng để truyền dữ liệu sang view
        List<DoctorStaffResponse> staffs = doctorStaffService.getAllDoctorStaff();
        model.addAttribute("staffs", staffs);
        return "redirect:/admin/doctor-staffs/list";// Redirect sang endpoint "/list" để thực hiện hiển thị danh sách bác sĩ một cách chính thức
    }

    @GetMapping("/list")
    public String listDoctorStaffAfterRedirect(@RequestParam(value = "q", required = false) String q,
                                               @RequestParam(value = "exclude", required = false) Long excludeId,
                                               Model model) {
        List<DoctorStaffResponse> staffs = (q == null || q.isBlank())
                ? doctorStaffService.getAllDoctorStaff()
                : doctorStaffService.searchDoctorStaff(q);
        if (excludeId != null) {
            staffs = staffs.stream().filter(s -> !excludeId.equals(s.getStaffId())).toList();
        }
        model.addAttribute("staffs", staffs);
        model.addAttribute("q", q);
        return "doctor-staff/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        // Nếu redirect có flashAttr, giữ nguyên để hiển thị lỗi; nếu F5/đi thẳng thì khởi tạo mới
        if (!model.containsAttribute("staff")) {//Kiểm tra xem có dữ liệu form chưa (từ lần nhập trước)
            model.addAttribute("staff", new DoctorStaffRequest());//tạo object rỗng để form hiển thị đúng
        }
        model.addAttribute("isEdit", false);//Phân biệt giữa form thêm mới (create) và form chỉnh sửa (edit).
        model.addAttribute("hospitals", hospitalRepo.findAll());
        model.addAttribute("departments", departmentRepo.findAll());
        model.addAttribute("roles", ROLE_NAMES);
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
            model.addAttribute("roles", java.util.List.of(
                    org.project.enums.StaffRole.DOCTOR,
                    org.project.enums.StaffRole.TECHNICIAN,
                    org.project.enums.StaffRole.SCHEDULING_COORDINATOR,
                    org.project.enums.StaffRole.PHARMACIST,
                    org.project.enums.StaffRole.INVENTORY_MANAGER,
                    org.project.enums.StaffRole.LAB_RECEIVER
            ).stream().map(Enum::name).toList());
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
            model.addAttribute("roles", java.util.List.of(
                    org.project.enums.StaffRole.DOCTOR,
                    org.project.enums.StaffRole.TECHNICIAN,
                    org.project.enums.StaffRole.SCHEDULING_COORDINATOR,
                    org.project.enums.StaffRole.PHARMACIST,
                    org.project.enums.StaffRole.INVENTORY_MANAGER,
                    org.project.enums.StaffRole.LAB_RECEIVER
            ).stream().map(Enum::name).toList());
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

    @GetMapping("/delete/{id}")
    public String deleteDoctorStaff(@PathVariable("id") Long id) {
        return "redirect:/admin/doctor-staffs/list?exclude=" + id;
    }
}
