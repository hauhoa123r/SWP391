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

    @GetMapping
    public String getPatientList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminPatientResponse> patientPage = adminPatientService.getAllPatients(pageable, keyword);

        model.addAttribute("patientPage", patientPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages", patientPage.getTotalPages());

        return "dashboard/patient";
    }

    @GetMapping("/detail/{id}")
    public String viewPatientDetail(@PathVariable Long id, Model model) {
        AdminPatientDetailResponse patientDetail = adminPatientService.getPatientDetail(id);
        model.addAttribute("patient", patientDetail);
        return "admin/patient/detail";
    }

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
        return "redirect:/patients/detail/" + id;
    }
}