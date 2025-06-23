package org.project.controller;

import org.project.model.response.DepartmentResponse;
import org.project.model.response.DoctorResponse;
import org.project.service.DepartmentService;
import org.project.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private final int PAGE_SIZE = 6;

    private DoctorService doctorService;
    private DepartmentService departmentService;

    @Autowired
    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Autowired
    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @ModelAttribute("departments")
    public List<DepartmentResponse> getDepartmentsByDoctorRole() {
        return departmentService.getAllHaveDoctor();
    }

    @GetMapping("/page/{pageIndex}")
    public String getAllStaffByPage(@PathVariable int pageIndex, Model model) {
        Page<DoctorResponse> doctorResponsePage = doctorService.getAll(pageIndex, PAGE_SIZE);
        model.addAttribute("doctors", doctorResponsePage.getContent());
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("totalPages", doctorResponsePage.getTotalPages());
        return "/frontend/doctor";
    }

    @GetMapping("/page/{pageIndex}/department/{departmentId}")
    public String getStaffByDepartment(@PathVariable int pageIndex,
                                       @PathVariable Long departmentId,
                                       Model model) {
        Page<DoctorResponse> doctorResponsePage = doctorService.getAllByDepartment(departmentId, pageIndex, PAGE_SIZE);
        model.addAttribute("doctors", doctorResponsePage.getContent());
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("totalPages", doctorResponsePage.getTotalPages());
        model.addAttribute("departmentId", departmentId);
        return "/frontend/doctor";
    }

    @GetMapping("/detail/{doctorId}")
    public String getDoctorDetail(@PathVariable Long doctorId, Model model) {
        DoctorResponse doctorResponse = doctorService.getDoctor(doctorId);
        model.addAttribute("doctor", doctorResponse);
        List<DoctorResponse> doctorResponses = doctorService.getColleagueDoctorsByDepartment(doctorResponse.getStaffEntity().getDepartmentEntity().getId(), doctorId);
        model.addAttribute("colleagueDoctors", doctorResponses);
        return "/frontend/doctor-details";
    }
}
