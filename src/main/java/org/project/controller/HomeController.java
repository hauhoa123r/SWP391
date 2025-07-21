package org.project.controller;

import java.util.List;

import org.project.entity.CartItemEntity;
import org.project.service.CartService;
import org.project.service.AppointmentService;
import org.project.service.DoctorService;
import org.project.service.ReviewService;
import org.project.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
	private final CartService cartService;

    @Autowired
    public void setServiceService(ServiceService serviceService) {
        this.serviceService = serviceService;
    }
	// inject cart service
	public HomeController(CartService cartService) {
		this.cartService = cartService;
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
	// use to view cart card on the nav bar
	@GetMapping
	public String viewCart(Model model) {
		Long userId = 2l;
		List<CartItemEntity> cartItems = cartService.getCart(userId);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("total", cartService.calculateTotal(userId));
		model.addAttribute("size", cartItems.size());
		return "frontend/index";
	}

    @GetMapping("/about-us")
    public String aboutUs(ModelMap modelMap) {
        modelMap.addAttribute("services", serviceService.getTopServices(4));
        modelMap.addAttribute("doctors", doctorService.getTop6Doctors());
        modelMap.addAttribute("reviews", reviewService.getTop5Reviews());
        return "frontend/about-us";
    }
	// hard delete from cart
	@PostMapping("/cart-card/delete")
	public String deleteCartItem(@RequestParam("productId") Long productId) {
		Long userId = 2l;
		cartService.removeItem(userId, productId);

		// Redirect back to home
		return "redirect:/";
	}
}
