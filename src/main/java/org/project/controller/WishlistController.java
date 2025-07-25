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
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "date_desc") String sort,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            Model model, 
            HttpSession session) {
        
        // Lấy tất cả items từ wishlist
        List<PharmacyResponse> allItems = wishlistService.getWishlistItems(getCurrentUserId(session));
        
        // Lọc theo tìm kiếm nếu có
        List<PharmacyResponse> filteredItems = allItems;
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            filteredItems = allItems.stream()
                    .filter(item -> item.getName().toLowerCase().contains(searchLower) || 
                                    (item.getDescription() != null && 
                                     item.getDescription().toLowerCase().contains(searchLower)))
                    .collect(Collectors.toList());
        }
        
        // Sắp xếp dữ liệu
        switch (sort) {
            case "name_asc":
                filteredItems.sort(Comparator.comparing(PharmacyResponse::getName));
                break;
            case "name_desc":
                filteredItems.sort(Comparator.comparing(PharmacyResponse::getName).reversed());
                break;
            case "price_asc":
                filteredItems.sort(Comparator.comparing(PharmacyResponse::getPrice));
                break;
            case "price_desc":
                filteredItems.sort(Comparator.comparing(PharmacyResponse::getPrice).reversed());
                break;
            case "date_asc":
                // Giả định rằng ID sẽ tăng theo thời gian tạo
                filteredItems.sort(Comparator.comparing(PharmacyResponse::getId));
                break;
            case "date_desc":
            default:
                // Giả định rằng ID sẽ tăng theo thời gian tạo
                filteredItems.sort(Comparator.comparing(PharmacyResponse::getId).reversed());
                break;
        }
        
        // Tính toán thông tin phân trang
        int totalItems = filteredItems.size();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        
        // Đảm bảo page trong phạm vi hợp lệ
        if (page < 0) {
            page = 0;
        } else if (page >= totalPages && totalPages > 0) {
            page = totalPages - 1;
        }
        
        // Lấy dữ liệu trang hiện tại
        List<PharmacyResponse> pagedItems;
        if (!filteredItems.isEmpty()) {
            int start = page * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, totalItems);
            pagedItems = filteredItems.subList(start, end);
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
