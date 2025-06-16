package org.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.model.response.PharmacyListResponse;
import org.project.projection.ProductViewProjection;
import org.project.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ShopController {

	@Autowired
	private PharmacyService pharmacyServiceImpl;
	
	
	@GetMapping("/shop")
	public ModelAndView shop(@RequestParam(value = "search", required = false) String search) {
		ModelAndView mv = new ModelAndView("frontend/shop");
		// If search is not empty, filter products by name
		if (search != null && !search.isEmpty()) {
			mv.addObject("products", pharmacyServiceImpl.findByName(search));
		} else {
			// If search is empty, show all products
			mv.addObject("products", pharmacyServiceImpl.getAllPharmacies());
		}
		return mv;
	}
	
	
	@PostMapping("/submit")
	public String search(@RequestParam("search") String search) {
		// Redirect to the shop page with the search query
		if (search != null && !search.isEmpty()) {
			return "redirect:/shop?search=" + search;
		}
		// If search is empty, redirect to the shop page without a search query
		return "redirect:/shop";
	}

	@GetMapping("/product-standard/")
	public ModelAndView product() {
		ModelAndView mv = new ModelAndView("frontend/product-standard");
		return mv;
	}
	
	@GetMapping("/product-standard/{id}") 
	public String getProduct(@PathVariable("id") Long id, Model model) {
		// Check if the ID is null or less than 1 
		if (id == null || id < 1) {
			throw new IllegalArgumentException("Invalid product ID: " + id);
		}
		// Fetch the product by ID
//		PharmacyListResponse product = pharmacyServiceImpl.findById(id);
		List<ProductViewProjection> product = pharmacyServiceImpl.findAllProductsWithFullInfo(id);  
		// Check if the product is null
		if (product == null) {
			throw new IllegalArgumentException("No product found with ID: " + id);
		}
		//Map of additional information (name-value pairs) 
		Map<String, String> additionalInfo = new HashMap<>(); 
		//iterate through the product's additional information and add it to the map 
		for (ProductViewProjection info : product) {
			//Get name 
			String name = info.getAdditionalInfoName(); 
			//Get value 
			String value = info.getAdditionalInfoValue(); 
			// If name is not null, add to the map 
			if (name != null && !name.isEmpty()) {
				//add to the map 
				additionalInfo.put(name, value);
			}
		}
		// Add the product to the model
		model.addAttribute("product", product.get(0)); // Assuming the projection returns a list, take the first item
		//add the additional information map to the model 
		model.addAttribute("additionalInfo", additionalInfo); 
		// Return the view name
		return "frontend/product-standard";
	}

	@GetMapping("/product-home")
	public ModelAndView productHome() {
		ModelAndView mv = new ModelAndView("frontend/product-home");
		// Fetch top 10 products for the home page 
		mv.addObject("products", pharmacyServiceImpl.findTop10Products());
		return mv;
	}

	@GetMapping("/cart")
	public ModelAndView cart() {
		ModelAndView mv = new ModelAndView("frontend/cart");
		return mv;
	}

	@GetMapping("/checkout")
	public ModelAndView checkout() {
		ModelAndView mv = new ModelAndView("frontend/checkout");
		return mv;
	}

	@GetMapping("/wishlist")
	public ModelAndView wishlist() {
		ModelAndView mv = new ModelAndView("frontend/wishlist");
		return mv;
	}

	@GetMapping("/my-account")
	public ModelAndView myAccount() {
		ModelAndView mv = new ModelAndView("frontend/my-account");
		return mv;
	}

	@GetMapping("/track-order")
	public ModelAndView trackOrder() {
		ModelAndView mv = new ModelAndView("frontend/track-order");
		return mv;
	}

	@GetMapping("/product-new")
	public ModelAndView productNew() {
		ModelAndView mv = new ModelAndView("frontend/product-new");
		return mv;
	}

	@GetMapping("/product-sale")
	public ModelAndView productSale() {
		ModelAndView mv = new ModelAndView("frontend/product-sale");
		return mv;
	}

	@GetMapping("/shop-left-sidebar")
	public ModelAndView shopLeftSidebar() {
		ModelAndView mv = new ModelAndView("frontend/shop-left-sidebar");
		return mv;
	}

	@GetMapping("/shop-right-sidebar")
	public ModelAndView shopRightSidebar() {
		ModelAndView mv = new ModelAndView("frontend/shop-right-sidebar");
		return mv;
	}

	@GetMapping("/shop-no-sidebar")
	public ModelAndView shopNoSidebar() {
		ModelAndView mv = new ModelAndView("frontend/shop-no-sidebar");
		return mv;
	}
}
