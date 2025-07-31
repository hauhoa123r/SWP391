package org.project.controller;

import org.project.model.dto.ServiceDTO;
import org.project.model.response.ServiceResponse;
import org.project.service.DepartmentService;
import org.project.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ServiceController {

    private ServiceService serviceService;
    private DepartmentService departmentService;

    @Autowired
    public void setServiceService(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @Autowired
    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/service")
    public String getAll() {
        return "/frontend/service";
    }

    @GetMapping("/service/detail/{id}")
    public String serviceDetail(@PathVariable Long id, Model model) {
        ServiceResponse productResponse = serviceService.getActiveService(id);
        model.addAttribute("service", productResponse);
        return "/frontend/service-detail";
    }

    @GetMapping("/admin/service")
    public String getAllForAdmin(ModelMap modelMap) {
        modelMap.put("departments", departmentService.getDepartments());
        modelMap.put("serviceDTO", new ServiceDTO());
        return "/dashboard/service";
    }
}
