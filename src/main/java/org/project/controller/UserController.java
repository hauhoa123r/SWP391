package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.config.ModelMapperConfig;
import org.project.entity.UserEntity;
import org.project.model.response.UserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @GetMapping("")
    public String hello(Model model) {
        return "frontend/product-home";
    }
    //mapping for admin's dashboard 
    @GetMapping("/admin/dashboard") 
    public ModelAndView adminDashboard() {
		ModelAndView mv = new ModelAndView("dashboard/index"); 
		return mv;
	}
    //mapping for patient's dashboard 
    @GetMapping("/patient/dashboard") 
    public ModelAndView patientDashboard() {
    	ModelAndView mv = new ModelAndView("dashboard/patient-dashboard"); 
    	return mv; 
    }
    //mapping for admin's crud of product 
    @GetMapping("/admin/product") 
    public ModelAndView adminProduct() {
		ModelAndView mv = new ModelAndView("dashboard/products");
		return mv;
	}
    
}
