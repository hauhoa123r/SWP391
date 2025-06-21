package org.project.controller;

import org.project.model.response.ServiceResponse;
import org.project.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/service")
public class ServiceController {

    private final int PAGE_SIZE = 9;

    private ServiceService serviceService;

    @Autowired
    public void setServiceService(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping("/page/{pageIndex}")
    public String service(@PathVariable int pageIndex, Model model) {
        Page<ServiceResponse> productRespsonsePage = serviceService.getServices(pageIndex, PAGE_SIZE);
        model.addAttribute("services", productRespsonsePage.getContent());
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("totalPages", productRespsonsePage.getTotalPages());
        return "/frontend/service";
    }

    @GetMapping("/detail/{id}")
    public String serviceDetail(@PathVariable Long id, Model model) {
        ServiceResponse productResponse = serviceService.getService(id);
        model.addAttribute("service", productResponse);
        return "/frontend/service-detail";
    }
}
