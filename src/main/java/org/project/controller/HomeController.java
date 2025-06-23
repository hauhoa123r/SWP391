package org.project.controller;

import java.util.List;

import org.project.entity.CartItemEntity;
import org.project.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/")
public class HomeController {
	
	private final CartService cartService;
	// inject cart service
	public HomeController(CartService cartService) {
        this.cartService = cartService;
    }
	
	@GetMapping
	public String viewCart(Model model) {
		Long userId = 2l;
		List<CartItemEntity> cartItems = cartService.getCart(userId);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("total", cartService.calculateTotal(userId));
		model.addAttribute("size",cartItems.size());
		return "frontend/index";
	}
}
