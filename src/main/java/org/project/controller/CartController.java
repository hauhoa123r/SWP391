package org.project.controller;

import java.util.List;

import org.project.entity.CartItemEntity;
import org.project.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

	private static final String SESSION_USER_ID = "userId";

	private final CartService cartService;
	public CartController(CartService cartService) {
        this.cartService = cartService;
    }

	private Long getUserId(HttpSession session) {
		Long userId = (Long) session.getAttribute(SESSION_USER_ID);
		userId = 12l;
		if (userId == null)
			throw new RuntimeException("User not logged in");
		return userId;
	}

	@GetMapping
	public String viewCart(HttpSession session, Model model) {
		Long userId = getUserId(session);
		List<CartItemEntity> cartItems = cartService.getCart(userId);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("total", cartService.calculateTotal(userId));
		return "cart";
	}

	@PostMapping("/add/{productId}")
	public String addItem(@PathVariable Long productId, @RequestParam(defaultValue = "1") Integer quantity,
			HttpSession session) {
		Long userId = getUserId(session);
		cartService.addItem(userId, productId, quantity);
		return "redirect:/cart";
	}

	@PostMapping("/remove/{productId}")
	public String removeItem(@PathVariable Long productId, HttpSession session) {
		Long userId = getUserId(session);
		cartService.removeItem(userId, productId);
		return "redirect:/cart";
	}

	@PostMapping("/update/{productId}")
	public String updateQuantity(@PathVariable Long productId, @RequestParam Integer quantity, HttpSession session) {
		Long userId = getUserId(session);
		cartService.updateQuantity(userId, productId, quantity);
		return "redirect:/cart";
	}

	@PostMapping("/applyCoupon")
	public String applyCoupon(@RequestParam String code, HttpSession session, Model model) {
		Long userId = getUserId(session);
		try {
			cartService.applyCoupon(userId, code);
		} catch (RuntimeException e) {
			model.addAttribute("couponError", e.getMessage());
		}
		return "redirect:/cart";
	}

	@GetMapping("/checkout")
	public String checkout() {
		return "checkout";
	}
}
