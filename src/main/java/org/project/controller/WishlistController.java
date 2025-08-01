package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.model.response.PharmacyResponse;
import org.project.service.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * Controller to handle wishlist related interactions (view, add, remove products).
 * <p>
 *  NOTE: For now the implementation uses a temporary hard-coded userId (1L).
 *  Replace this with the authenticated user's ID once the security layer is ready.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * Display the wishlist page.
     */
    @GetMapping("/wishlist")
    public String viewWishlist(Model model, HttpSession session) {
        List<PharmacyResponse> items = wishlistService.getWishlistItems(getCurrentUserId(session));
        model.addAttribute("items", items);
        return "wishlist"; // renders templates/wishlist.html
    }

    /**
     * Add a product to the wishlist then redirect back to the previous page.
     */
    @PostMapping("/wishlist/add")
    public String addToWishlist(@RequestParam("productId") Long productId,
                                @RequestHeader(value = "Referer", required = false) String referer,
                                RedirectAttributes redirectAttrs,
                                HttpSession session) {
        wishlistService.addProduct(getCurrentUserId(session), productId);
        redirectAttrs.addFlashAttribute("wishlistMessage", "Đã thêm sản phẩm vào wishlist thành công!");
        return redirectBack(referer);
    }

    /**
     * Remove a product from the wishlist then redirect back.
     */
    @PostMapping("/wishlist/remove")
    public String removeFromWishlist(@RequestParam("productId") Long productId,
                                     @RequestHeader(value = "Referer", required = false) String referer,
                                     RedirectAttributes redirectAttrs,
                                     HttpSession session) {
        wishlistService.removeProduct(getCurrentUserId(session), productId);
        redirectAttrs.addFlashAttribute("wishlistMessage", "Đã xoá sản phẩm khỏi wishlist!");
        return redirectBack(referer);
    }
    
    /**
     * Update quantity of a product in the wishlist.
     */
    @PostMapping("/wishlist/update-quantity")
    public String updateQuantity(@RequestParam("productId") Long productId,
                                @RequestParam("quantity") Integer quantity,
                                @RequestHeader(value = "Referer", required = false) String referer,
                                RedirectAttributes redirectAttrs,
                                HttpSession session) {
        wishlistService.updateQuantity(getCurrentUserId(session), productId, quantity);
        redirectAttrs.addFlashAttribute("wishlistMessage", "Số lượng sản phẩm đã được cập nhật!");
        return redirectBack(referer);
    }

    /** Redirect helper: fallback to /wishlist if no referer present */
    private String redirectBack(String referer) {
        return referer != null ? "redirect:" + referer : "redirect:/wishlist";
    }

    /** Get current authenticated user ID from session */
    private Long getCurrentUserId(HttpSession session) {
        // TODO: Replace with authenticated user ID once security is integrated
        Object userId = session.getAttribute("userId");
        return userId != null ? (Long) userId : 1L; // Default to user ID 1 if not found
    }
}
