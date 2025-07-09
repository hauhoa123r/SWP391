package org.project.controller;

import org.project.model.response.ServiceResponse;
import org.project.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/service")
public class ServiceController {

    private ServiceService serviceService;

    @Autowired
    public void setServiceService(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public String getAll() {
        return "/frontend/service";
    }

    @GetMapping("/detail/{id}")
    public String serviceDetail(@PathVariable Long id, Model model) {
        ServiceResponse productResponse = serviceService.getActiveService(id);
        model.addAttribute("service", productResponse);
        return "/frontend/service-detail";
    }
}
