package org.project.controller;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.project.entity.CartItemEntity;
import org.project.entity.CartItemEntityId;
import org.project.entity.CouponEntity;
import org.project.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

	// private static final String SESSION_USER_ID = "userId";

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	// view all cart items of the user
	// including the total amount of money and number of item in cart
	@GetMapping
	public String viewCart(Model model) {
		Long userId = 2l;
		List<CartItemEntity> cartItems = cartService.getCart(userId);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("total", cartService.calculateTotal(userId));
		model.addAttribute("size", cartItems.size());
		return "frontend/cart";
	}

	// hard delete from cart
	@PostMapping("/delete")
	public String deleteCartItem(@RequestParam("productId") Long productId) {
		Long userId = 2l;
		cartService.removeItem(userId, productId);

		// Redirect back to cart
		return "redirect:/cart";
	}

	// change item quantity by pressing + -
	@PostMapping("/update")
	public String updateCartItemQuantity(@RequestParam("cartId") Long cartId, @RequestParam("userId") Long userId,
			@RequestParam("action") String action) {
		userId = 2l;
		CartItemEntityId id = new CartItemEntityId(cartId, userId);
		CartItemEntity item = cartService.getItemById(id);
		if (item == null) {
			System.out.println("no cart item found"); // debugging
			return "redirect:/cart";
		}

		int quantity = item.getQuantity();

		if ("increment".equals(action)) {
			item.setQuantity(quantity + 1);
		} else if ("decrement".equals(action) && quantity > 1) {
			item.setQuantity(quantity - 1);
		}

		cartService.updateItem(item);
		return "redirect:/cart";
	}

	// change item quantity by directly entering value
	@PostMapping("/update-quantity")
	public String updateCartItemQuantity(@RequestParam("cartId") Long cartId, @RequestParam("userId") Long userId,
			@RequestParam("quantity") int quantity) {

		CartItemEntityId id = new CartItemEntityId(cartId, userId);
		CartItemEntity item = cartService.getItemById(id);

		if (item != null && quantity > 0) {
			item.setQuantity(quantity);
			cartService.updateItem(item);
		}
		return "redirect:/cart";
	}

	// TODO: need to finish (help)
	// apply coupons (check if existed)
	@PostMapping("/apply-coupon")
	public String applyCoupon(@RequestParam("cartId") Long cartId, @RequestParam("") String couponCode) {

		return "redirect:/cart";
	}

	@GetMapping("/checkout")
	public String checkout() {
		return "/frontend/checkout";
	}

}
