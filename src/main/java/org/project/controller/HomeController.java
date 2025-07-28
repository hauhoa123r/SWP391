package org.project.controller;

import org.project.entity.UserEntity;
import org.project.enums.StaffRole;
import org.project.enums.UserRole;
import org.project.security.AccountDetails;
import org.project.service.AppointmentService;
import org.project.service.DoctorService;
import org.project.service.ReviewService;
import org.project.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

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
    public String showIndexPage(ModelMap modelMap) {
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

    @GetMapping("/home")
    public String getHomeByRole(@AuthenticationPrincipal AccountDetails accountDetails) {
        if (accountDetails == null) {
            return "redirect:/login";
        }

        UserEntity userEntity = accountDetails.getUserEntity();

        if (userEntity == null) {
            return "redirect:/login";
        }

        // TODO: Sửa redirect cho các role USER
        Map<UserRole, String> roleToRedirect = Map.of(
                UserRole.ADMIN, "redirect:/admin",
                UserRole.PATIENT, "redirect:/patient/showList"
        );
        if (roleToRedirect.containsKey(userEntity.getUserRole())) {
            return roleToRedirect.get(userEntity.getUserRole());
        }
        // TODO: Sửa redirect cho các role STAFF
        Map<StaffRole, String> roleToRedirectStaff = Map.of(
                StaffRole.DOCTOR, "redirect:/doctor",
                StaffRole.PHARMACIST, "redirect:/staff/pharmacy",
                StaffRole.TECHNICIAN, "redirect:/lab/homepage",
                StaffRole.SCHEDULING_COORDINATOR, "redirect:/staff/schedule",
                StaffRole.INVENTORY_MANAGER, "redirect:/staff/inventory"
        );
        if (roleToRedirectStaff.containsKey(userEntity.getStaffEntity().getStaffRole())) {
            return roleToRedirectStaff.get(userEntity.getStaffEntity().getStaffRole());
        }

        return "redirect:/";
    }
}
