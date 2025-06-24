package org.project.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.project.config.ModelMapperConfig;
import org.project.entity.UserEntity;
import org.project.model.dto.ProductViewDTO;
import org.project.model.response.UserLoginResponse;
import org.project.repository.impl.PharmacyRepositoryImpl;
import org.project.repository.impl.custom.PharmacyRepositoryCustom;
import org.project.service.impl.PharmacyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
	
	@Autowired 
	private PharmacyServiceImpl pharmacyServiceImpl; 
	
	
	@Autowired 
	private PharmacyRepositoryImpl pharmacyRepositoryCustom; 
	
	@GetMapping("")
	public String hello(Model model) {
		return "redirect:/product-home";
	}

	// mapping for admin's dashboard
	@GetMapping("/admin/dashboard")
	public ModelAndView adminDashboard() {
		ModelAndView mv = new ModelAndView("dashboard/index");
		return mv;
	}

	// mapping for patient's dashboard
	@GetMapping("/patient/dashboard")
	public ModelAndView patientDashboard() {
		ModelAndView mv = new ModelAndView("dashboard/patient-dashboard");
		return mv;
	}

	// mapping for admin's crud of product
	@GetMapping("/admin/product")
	public ModelAndView adminProduct(@RequestParam(defaultValue = "1") Integer page) { 
		ModelAndView mv = new ModelAndView("dashboard/products");
		int pageSize = 7; // Number of products per page 
		int offset = (page - 1) * pageSize; // Calculate the offset for pagination 
		// Fetch paginated products from the custom repository 
		List<ProductViewDTO> products = pharmacyRepositoryCustom.getPagedProducts(pageSize, offset); 
		//Get total number of products for pagination 
		Long totalProducts = pharmacyServiceImpl.countProducts(); 
		//Calculate total pages 
		Long totalPages = (totalProducts + pageSize - 1) / pageSize; // Ceiling division 
		// Add products and pagination info to the model 
		mv.addObject("products", products); 
		mv.addObject("currentPage", page); 
		mv.addObject("totalPages", totalPages); 
		mv.addObject("totalProducts", totalProducts); 
		return mv;
	}
	
	//post-mapping for admin's product delete 
	@PostMapping("/admin/product/delete/{id}")
	public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	    pharmacyServiceImpl.deleteById(id); 
	    redirectAttributes.addFlashAttribute("success", "Product deleted successfully.");
	    return "redirect:/admin/product";
	}
	
	// mapping for admin's appointments
	@GetMapping("/admin/appointment")
	public ModelAndView adminAppointment() {
		ModelAndView mv = new ModelAndView("dashboard/appointment");
		return mv;
	}

	// mapping for admin's report
	@GetMapping("/admin/report")
	public ModelAndView adminPharmacy() {
		ModelAndView mv = new ModelAndView("dashboard/report");
		return mv;
	}

	// mapping for admin's doctors
	@GetMapping("/admin/doctor")
	public ModelAndView adminReview() {
		ModelAndView mv = new ModelAndView("dashboard/doctors");
		return mv;
	}
	//mapping for admin's patients view 
	@GetMapping("/admin/patient") 
	public ModelAndView adminPatient() {
		ModelAndView mv = new ModelAndView("dashboard/patient");
		return mv;
	} 
	//mapping for admin's categories view 
	@GetMapping("/admin/category") 
	public ModelAndView adminCategory() {
		ModelAndView mv = new ModelAndView("dashboard/category");
		return mv;
	} 
	//mapping for admin's payments 
	@GetMapping("/admin/payment") 
	public ModelAndView adminPayment() {
		ModelAndView mv = new ModelAndView("dashboard/payment");
		return mv;
	} 
}
