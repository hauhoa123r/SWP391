package org.project.controller;

import org.project.entity.CartItemEntity;
import org.project.service.CartItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartItemController {

    private static final String SESSION_CART_ID = "cartId";

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    private Long getOrCreateCartId(HttpSession session) {
        Long cartId = (Long) session.getAttribute(SESSION_CART_ID);
        if (cartId == null) {
            CartItemEntity cart = CartItemEntity.getCart(null);
            cartId = cart.getId();
            session.setAttribute(SESSION_CART_ID, cartId);
        }
        return cartId;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Long cartId = getOrCreateCartId(session);
        Cart cart = cartService.getCart(cartId);
        model.addAttribute("cart", cart);
        model.addAttribute("total", cartService.calculateTotal(cartId));
        return "cart";
    }

    @PostMapping("/add/{productId}")
    public String addItem(@PathVariable Long productId,
                          @RequestParam(defaultValue = "1") int quantity,
                          HttpSession session) {
        Long cartId = getOrCreateCartId(session);
        cartService.addItem(cartId, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeItem(@PathVariable Long productId, HttpSession session) {
        Long cartId = getOrCreateCartId(session);
        cartItemService.removeItem(cartId, productId);
        return "redirect:/cart";
    }

    @PostMapping("/update/{productId}")
    public String updateQuantity(@PathVariable Long productId,
                                 @RequestParam int quantity,
                                 HttpSession session) {
        Long cartId = getOrCreateCartId(session);
        cartService.updateQuantity(cartId, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/applyCoupon")
    public String applyCoupon(@RequestParam String code,
                              HttpSession session,
                              Model model) {
        Long cartId = getOrCreateCartId(session);
        try {
            cartService.applyCoupon(cartId, code);
        } catch (RuntimeException e) {
            model.addAttribute("couponError", e.getMessage());
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "redirect:/checkout";
    }
}
