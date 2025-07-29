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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Enumeration;

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
    private static final int PAGE_SIZE = 10; // Số sản phẩm trên một trang

    /**
     * Display the wishlist page.
     */
    @GetMapping("/wishlist")
    public String viewWishlist(
            @RequestParam(value = "searchQuery", required = false) String searchQuery,
            @RequestParam(value = "sortBy", required = false, defaultValue = "name_asc") String sortBy,
            @RequestParam(value = "filterStock", required = false, defaultValue = "all") String filterStock,
            Model model,
            HttpSession session) {
        
        // Lấy tất cả items từ wishlist với các tham số lọc
        List<PharmacyResponse> allItems = wishlistService.getWishlistItems(getCurrentUserId(session), searchQuery, sortBy, filterStock);
        
        // Tính toán thông tin phân trang
        int totalItems = allItems.size();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        
        // Đảm bảo page trong phạm vi hợp lệ
        int page = 0; // Default page
        
        // Lấy dữ liệu trang hiện tại
        List<PharmacyResponse> pagedItems;
        if (!allItems.isEmpty()) {
            int start = page * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, totalItems);
            pagedItems = allItems.subList(start, end);
        } else {
            pagedItems = new ArrayList<>();
        }
        
        // Thêm thuộc tính vào model
        model.addAttribute("items", pagedItems);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", totalItems);
        
        return "/wishlist"; // renders templates/frontend/wishlist.html
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
                                     RedirectAttributes redirectAttrs,
                                     HttpSession session) {
        log.info("Received request to remove productId: {} from wishlist", productId);
        log.info("Product ID nhận được: {}", productId); // Thêm log để kiểm tra productId
        Long userId = getCurrentUserId(session);
        log.info("User ID from session: {}", userId);
        boolean removed = wishlistService.removeProduct(userId, productId);
        if (removed) {
            redirectAttrs.addFlashAttribute("wishlistMessage", "Đã xoá sản phẩm khỏi wishlist!");
        } else {
            redirectAttrs.addFlashAttribute("wishlistMessage", "Không thể xoá sản phẩm khỏi wishlist. Vui lòng thử lại.");
        }
        return redirectToReferer();
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

    /**
     * Remove selected products from the wishlist then redirect back.
     */
    @PostMapping("/wishlist/remove-selected")
    public String removeSelectedFromWishlist(@RequestParam(value = "selectedItems", required = false) List<Long> selectedItems,
                                             RedirectAttributes redirectAttrs,
                                             HttpSession session) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            redirectAttrs.addFlashAttribute("wishlistMessage", "Không có sản phẩm nào được chọn để xóa.");
            return redirectToReferer();
        }

        Long userId = getCurrentUserId(session);
        int removedCount = 0;
        for (Long productId : selectedItems) {
            boolean removed = wishlistService.removeProduct(userId, productId);
            if (removed) {
                removedCount++;
            }
        }

        if (removedCount > 0) {
            redirectAttrs.addFlashAttribute("wishlistMessage", "Đã xóa " + removedCount + " sản phẩm khỏi wishlist!");
        } else {
            redirectAttrs.addFlashAttribute("wishlistMessage", "Không thể xóa các sản phẩm đã chọn khỏi wishlist. Vui lòng thử lại.");
        }
        return redirectToReferer();
    }

    /** Redirect helper: fallback to /wishlist if no referer present */
    private String redirectBack(String referer) {
        return referer != null ? "redirect:" + referer : "redirect:/wishlist";
    }

    private String redirectToReferer() {
        return "redirect:/wishlist";
    }       

    /** Get current authenticated user ID from session */
    private Long getCurrentUserId(HttpSession session) {
        log.info("Getting current user ID from session, session id: {}", session.getId());
        // Log tất cả các attribute trong session để debug
        Enumeration<String> attributeNames = session.getAttributeNames();
        log.info("Session attributes:");
        while (attributeNames.hasMoreElements()) {
            String attrName = attributeNames.nextElement();
            try {
                Object attrValue = session.getAttribute(attrName);
                log.info("  {} = {}", attrName, attrValue != null ? attrValue.toString() : "null");
            } catch (Exception e) {
                log.warn("  {} = [unable to display value due to exception: {}]", attrName, e.getMessage());
            }
        }
        // TODO: Replace with authenticated user ID once security is integrated
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.warn("No userId found in session, using default value and setting it");
            userId = 1L; // Default for demo, replace with proper authentication
            session.setAttribute("userId", userId);
            // TODO: Ensure this matches a valid user ID in your database
        }
        log.info("Returning userId: {}", userId);
        return userId;
    }
}
