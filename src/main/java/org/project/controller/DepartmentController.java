package org.project.controller;

import org.project.service.AppointmentService;
import org.project.service.DepartmentService;
import org.project.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DepartmentController {

    private DepartmentService departmentService;
    private AppointmentService appointmentService;
    private DoctorService doctorService;

    @Autowired
    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Autowired
    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Autowired
    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @RequestMapping("/department")
    public String getAll() {
        return "/frontend/department";
    }

    @RequestMapping("/department/detail/{departmentId}")
    public String getDepartmentDetail(@PathVariable Long departmentId, ModelMap modelMap) {
        modelMap.addAttribute("department", departmentService.getDepartment(departmentId));
        modelMap.addAttribute("completedAppointmentsCount",
                appointmentService.countCompletedAppointmentsByDepartment(departmentId));
        modelMap.addAttribute("doctors", doctorService.getTop6DoctorsByDepartment(departmentId));
        return "/frontend/department-detail";
    }
}
