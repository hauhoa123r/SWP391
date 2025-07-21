package org.project.controller;

import org.project.model.response.DoctorResponse;
import org.project.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class DoctorController {

    private DoctorService doctorService;

    @Autowired
    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/doctor")
    public String getAll() {
        return "/frontend/doctor";
    }

    @GetMapping("/doctor/detail/{doctorId}")
    public String getDoctorDetail(@PathVariable Long doctorId, Model model) {
        DoctorResponse doctorResponse = doctorService.getDoctor(doctorId);
        model.addAttribute("doctor", doctorResponse);
        List<DoctorResponse> doctorResponses = doctorService.getColleagueDoctorsByDepartment(doctorResponse.getStaffEntity().getDepartmentEntity().getId(), doctorId);
        model.addAttribute("colleagueDoctors", doctorResponses);
        return "/frontend/doctor-detail";
    }
}
