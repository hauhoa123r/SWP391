package org.project.controller;

import org.project.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import ch.qos.logback.core.model.Model;

@Controller
public class ShopController {

    @Autowired
    private PharmacyService pharmacyServiceImpl;

    @GetMapping("/shop")
    public ModelAndView shop() {
        ModelAndView mv = new ModelAndView("shop");
        mv.addObject("products", pharmacyServiceImpl.getAllPharmacies());
        return mv;
    }
    
    @GetMapping("/product-standard") 
	public ModelAndView product() {
		ModelAndView mv = new ModelAndView("product-standard"); 
		return mv; 
	}
    
    @GetMapping("/product-home") 
    public ModelAndView productHome() {
    	ModelAndView mv = new ModelAndView("product-home"); 
    	return mv; 
    }
    
    @GetMapping("/cart")
    public ModelAndView cart() {
    	ModelAndView mv = new ModelAndView("cart"); 
    	return mv; 
    }
    
    @GetMapping("/checkout") 
    public ModelAndView checkout() {
    	ModelAndView mv = new ModelAndView("checkout"); 
    	return mv; 
    }
    
    @GetMapping("/wishlist") 
    public ModelAndView wishlist() {
    	ModelAndView mv = new ModelAndView("wishlist"); 
    	return mv; 
    }
    
    @GetMapping("/my-account") 
    public ModelAndView myAccount() {
    	ModelAndView mv = new ModelAndView("my-account"); 
    	return mv; 
    }
    
    @GetMapping("/track-order") 
    public ModelAndView trackOrder() {
    	ModelAndView mv = new ModelAndView("track-order"); 
    	return mv; 
    }
    
    @GetMapping("/product-new") 
    public ModelAndView productNew() {
    	ModelAndView mv = new ModelAndView("product-new"); 
    	return mv; 
    }
    
    @GetMapping("/product-sale") 
    public ModelAndView productSale() {
    	ModelAndView mv = new ModelAndView("product-sale"); 
    	return mv; 
    }
    
    @GetMapping("/shop-left-sidebar") 
    public ModelAndView shopLeftSidebar() {
    	ModelAndView mv = new ModelAndView("shop-left-sidebar"); 
    	return mv; 
    }
    
    @GetMapping("/shop-right-sidebar") 
    public ModelAndView shopRightSidebar() {
    	ModelAndView mv = new ModelAndView("shop-right-sidebar"); 
    	return mv; 
    }
    
    @GetMapping("/shop-no-sidebar")
    public ModelAndView shopNoSidebar() {
    	ModelAndView mv = new ModelAndView("shop-no-sidebar"); 
    	return mv; 
    }
  }

