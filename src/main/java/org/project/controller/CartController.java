//package org.project.controller;
//
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.project.model.response.CartItemResponse;
//import org.project.service.CartService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.List;
//
//@Slf4j
//@Controller
//@RequiredArgsConstructor
//public class CartController {
//
//    private final CartService cartService;
//
//    /**
//     * Display the cart page with all items
//     */
//    @GetMapping("/cart")
//    public String viewCart(Model model, HttpSession session) {
//        Long userId = getCurrentUserId(session);
//        List<CartItemResponse> cartItems = cartService.getCartItems(userId);
//        double total = cartService.getCartTotal(userId);
//
//        model.addAttribute("cartItems", cartItems);
//        model.addAttribute("cartTotal", total);
//
//        return "cart"; // renders templates/cart.html
//    }
//
//    /**
//     * Add a product to the cart
//     */
//    @PostMapping("/cart/add")
//    public String addToCart(@RequestParam("productId") Long productId,
//                            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
//                            @RequestHeader(value = "Referer", required = false) String referer,
//                            RedirectAttributes redirectAttrs,
//                            HttpSession session) {
//        Long userId = getCurrentUserId(session);
//        cartService.addToCart(userId, productId, quantity);
//
//        redirectAttrs.addFlashAttribute("cartMessage", "Đã thêm sản phẩm vào giỏ hàng!");
//        return redirectBack(referer);
//    }
//
//    /**
//     * Remove a product from the cart
//     */
//    @PostMapping("/cart/remove")
//    public String removeFromCart(@RequestParam("productId") Long productId,
//                                @RequestHeader(value = "Referer", required = false) String referer,
//                                RedirectAttributes redirectAttrs,
//                                HttpSession session) {
//        Long userId = getCurrentUserId(session);
//        cartService.removeFromCart(userId, productId);
//
//        redirectAttrs.addFlashAttribute("cartMessage", "Đã xóa sản phẩm khỏi giỏ hàng!");
//        return redirectBack(referer);
//    }
//
//    /**
//     * Update the quantity of a product in the cart
//     */
//    @PostMapping("/cart/update")
//    public String updateQuantity(@RequestParam("productId") Long productId,
//                                @RequestParam("quantity") Integer quantity,
//                                @RequestHeader(value = "Referer", required = false) String referer,
//                                RedirectAttributes redirectAttrs,
//                                HttpSession session) {
//        Long userId = getCurrentUserId(session);
//        cartService.updateQuantity(userId, productId, quantity);
//
//        redirectAttrs.addFlashAttribute("cartMessage", "Đã cập nhật số lượng sản phẩm!");
//        return redirectBack(referer);
//    }
//
//    /**
//     * Clear all items from the cart
//     */
//    @PostMapping("/cart/clear")
//    public String clearCart(@RequestHeader(value = "Referer", required = false) String referer,
//                           RedirectAttributes redirectAttrs,
//                           HttpSession session) {
//        Long userId = getCurrentUserId(session);
//        cartService.clearCart(userId);
//
//        redirectAttrs.addFlashAttribute("cartMessage", "Đã xóa tất cả sản phẩm khỏi giỏ hàng!");
//        return redirectBack(referer);
//    }
//
//    /**
//     * Get cart count for AJAX requests
//     */
//    @GetMapping("/cart/count")
//    @ResponseBody
//    public int getCartCount(HttpSession session) {
//        Long userId = getCurrentUserId(session);
//        return cartService.getCartItemCount(userId);
//    }
//
//    /**
//     * Get cart items for AJAX requests
//     */
//    @GetMapping("/cart/items")
//    @ResponseBody
//    public List<CartItemResponse> getCartItems(HttpSession session) {
//        Long userId = getCurrentUserId(session);
//        return cartService.getCartItems(userId);
//    }
//
//    /** Redirect helper: fallback to /cart if no referer present */
//    private String redirectBack(String referer) {
//        if (referer != null && !referer.isBlank()) {
//            return "redirect:" + referer;
//        }
//        return "redirect:/cart";
//    }
//
//    private Long getCurrentUserId(HttpSession session) {
//        Long userId = (Long) session.getAttribute("userId");
//        return userId != null ? userId : 1L; // Default to user ID 1 if not logged in
//    }
//}