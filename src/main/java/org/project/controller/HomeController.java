package org.project.controller;

import java.util.List;

import org.project.entity.CartItemEntity;
import org.project.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
@RequestMapping("/")
public class HomeController {
	
	private final CartService cartService;
	// inject cart service
	public HomeController(CartService cartService) {
        this.cartService = cartService;
    }
	// use to view cart card on the nav bar
	@GetMapping
	public String viewCart(Model model) {
		Long userId = 2l;
		List<CartItemEntity> cartItems = cartService.getCart(userId);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("total", cartService.calculateTotal(userId));
		model.addAttribute("size",cartItems.size());
		return "frontend/index";
	}
	//hard delete from cart
		@PostMapping("/cart-card/delete")
		public String deleteCartItem(@RequestParam("productId") Long productId) {
		    Long userId=2l;
		    cartService.removeItem(userId, productId);
		    
		    // Redirect back to home
		    return "redirect:/";
		}
}
