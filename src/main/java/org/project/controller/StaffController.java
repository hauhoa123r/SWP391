package org.project.controller;

import org.project.model.dto.StaffDTO;
import org.project.service.DepartmentService;
import org.project.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffController {
    private DepartmentService departmentService;
    private HospitalService hospitalService;

    @Autowired
    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Autowired
    public void setHospitalService(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/admin/staff")
    public String staffManagement(ModelMap modelMap) {
        modelMap.put("staffDTO", new StaffDTO());
        modelMap.put("departments", departmentService.getDepartments());
        modelMap.put("hospitals", hospitalService.getHospitals());
        return "/dashboard/staff";
    }
}
