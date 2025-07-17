package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.model.request.AdminPatientUpdateRequest;
import org.project.model.response.AdminPatientDetailResponse;
import org.project.model.response.AdminPatientResponse;
import org.project.service.AdminPatientService;
import org.project.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/patients")
@RequiredArgsConstructor
public class AdminPatientController {

    private final AdminPatientService adminPatientService;
    private final UserService userService;

    // ✅ Danh sách bệnh nhân với tìm kiếm và phân trang
    @GetMapping
    public String getPatientList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "name") String field,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminPatientResponse> patientPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            // Không có từ khóa → lấy toàn bộ
            patientPage = adminPatientService.getAllPatients(pageable);
        } else {
            // Có từ khóa → lọc theo field
            patientPage = adminPatientService.getAllPatients(pageable, keyword.trim(), field);
        }

        model.addAttribute("patientPage", patientPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", patientPage.getTotalPages());
        model.addAttribute("field", field);
        model.addAttribute("keyword", keyword);

        return "dashboard/patient";
    }



    // ✅ Xem chi tiết bệnh nhân
    @GetMapping("/detail/{id}")
    public String viewPatientDetail(@PathVariable Long id, Model model) {
        AdminPatientDetailResponse patientDetail = adminPatientService.getPatientDetail(id);
        model.addAttribute("patient", patientDetail);
        return "admin/patient/detail";
    }

    // ✅ Hiển thị form chỉnh sửa bệnh nhân
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        model.addAttribute("patientId", id);
        model.addAttribute("patientRequest", adminPatientService.getUpdateForm(id));
        model.addAttribute("users", userService.getAllUsers(page, size));
        return "admin/patient/edit";
    }

    // ✅ Cập nhật thông tin bệnh nhân
    @PostMapping("/edit/{id}")
    public String updatePatient(@PathVariable Long id,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @ModelAttribute("patientRequest") @Valid AdminPatientUpdateRequest request,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers(page, size));
            return "admin/patient/edit";
        }
        adminPatientService.updatePatient(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công");
        return "redirect:/admin/patients/detail/" + id;
    }
    @GetMapping("/deleted")
    public String getDeletedPatients(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminPatientResponse> deletedPatients = adminPatientService.getDeletedPatients(pageable);

        model.addAttribute("patientPage", deletedPatients);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", deletedPatients.getTotalPages());
        return "dashboard/patient-deleted"; // view HTML riêng
    }
    @PostMapping("/soft-delete/{id}")
    public String softDeletePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminPatientService.softDeletePatient(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tạm thời bệnh nhân");
        return "redirect:/admin/patients";
    }
    @PostMapping("/restore/{id}")
    public String restorePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminPatientService.restorePatient(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã khôi phục bệnh nhân");
        return "redirect:/admin/patients/deleted";
    }
    @PostMapping("/delete-permanent/{id}")
    public String deletePermanently(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminPatientService.deletePermanently(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa vĩnh viễn bệnh nhân");
        return "redirect:/admin/patients/deleted";
    }




}
