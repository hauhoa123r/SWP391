package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.model.request.AdminPatientRequest;
import org.project.model.response.AdminPatientResponse;
import org.project.model.response.PageResponse;
import org.project.service.AdminPatientService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patients")
public class AdminPatientController {

    private final AdminPatientService adminPatientService;

    @GetMapping
    public String viewPatients(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<AdminPatientResponse> response = adminPatientService.getAllPatients(pageable);
        model.addAttribute("patients", response.getContent().getContent());
        model.addAttribute("currentPage", response.getCurrentPage());
        model.addAttribute("totalPages", response.getTotalPages());
        return "dashboard/patient";
    }

    @GetMapping("/search")
    public String searchPatients(@RequestParam String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, size);
        AdminPatientRequest req = AdminPatientRequest.builder().keyword(keyword).build();
        PageResponse<AdminPatientResponse> response = adminPatientService.searchPatients(req, pageable);
        model.addAttribute("patients", response.getContent().getContent());//gửi ds bệnh nhân ở trang hiện tại
        model.addAttribute("currentPage", response.getCurrentPage());//gửi trang hiện tại(Để view biết trang nào đang được hiển thị.)
        model.addAttribute("totalPages", response.getTotalPages());  // tổng số trang để tạo nút phân trang
        model.addAttribute("keyword", keyword);
        return "dashboard/patient";
    }

    @GetMapping("/{id}")
    public String getPatientById(@PathVariable Long id, Model model) {
        AdminPatientResponse patient = adminPatientService.getPatientById(id);
        model.addAttribute("patient", patient);
        return "dashboard/patient";
    }

}