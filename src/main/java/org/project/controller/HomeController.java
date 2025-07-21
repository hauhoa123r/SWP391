package org.project.controller;

import org.project.service.AppointmentService;
import org.project.service.DoctorService;
import org.project.service.ReviewService;
import org.project.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private DoctorService doctorService;
    private ServiceService serviceService;
    private AppointmentService appointmentService;
    private ReviewService reviewService;

    @Autowired
    public void setDoctorService(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Autowired
    public void setServiceService(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @Autowired
    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Autowired
    public void setReviewService(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String patientHome(ModelMap modelMap) {
        modelMap.addAttribute("headDoctor", doctorService.getTop1Doctor());
        modelMap.addAttribute("services", serviceService.getTopServices(3));
        modelMap.addAttribute("doctors", doctorService.getTop6Doctors());
        modelMap.addAttribute("reviews", reviewService.getTop5Reviews());
        modelMap.addAttribute("serviceCount", serviceService.countActiveService());
        modelMap.addAttribute("appointmentCount", appointmentService.countCompletedAppointments());
        modelMap.addAttribute("reviewCount", reviewService.count5StarReviews());
        return "frontend/index";
    }

    @GetMapping("/about-us")
    public String aboutUs(ModelMap modelMap) {
        modelMap.addAttribute("services", serviceService.getTopServices(4));
        modelMap.addAttribute("doctors", doctorService.getTop6Doctors());
        modelMap.addAttribute("reviews", reviewService.getTop5Reviews());
        return "frontend/about-us";
    }
}
