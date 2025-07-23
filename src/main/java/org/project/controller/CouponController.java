package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.enums.DiscountType;
import org.project.model.dto.CouponDTO;
import org.project.service.CouponService;
import org.project.service.UserCouponService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final UserCouponService userCouponService;
    
    /**
     * Hiển thị danh sách coupon
     */
    @GetMapping("/coupon-list")
    public String getCouponList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            Model model) {
            
        log.info("Fetching coupons with page={}, size={}, keyword={}, sortBy={}, sortDir={}", 
                 page, size, keyword, sortBy, sortDir);
                 
        Page<CouponDTO> couponPage;
        if (keyword != null && !keyword.isEmpty()) {
            couponPage = couponService.searchCoupons(keyword, page, size, sortBy, sortDir);
            model.addAttribute("keyword", keyword);
        } else {
            couponPage = couponService.findAllCoupons(page, size, sortBy, sortDir);
        }
        
        model.addAttribute("coupons", couponPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", couponPage.getTotalPages());
        model.addAttribute("totalItems", couponPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "templates_storage/coupon-list";
    }
    
    /**
     * Hiển thị form tạo mới coupon
     */
    @GetMapping("/create-coupon")
    public String showCreateForm(Model model) {
        model.addAttribute("coupon", new CouponDTO());
        model.addAttribute("discountTypes", Arrays.asList(DiscountType.values()));
        
        return "templates_storage/create-coupon";
    }
    
    /**
     * Xử lý tạo mới coupon
     */
    @PostMapping("/create-coupon")
    public String createCoupon(@ModelAttribute CouponDTO coupon, RedirectAttributes redirectAttributes) {
        log.info("Creating new coupon: {}", coupon.getCode());
        
        try {
            // Nếu discountType không được thiết lập, sử dụng giá trị mặc định
            if (coupon.getDiscountType() == null) {
                coupon.setDiscountType(DiscountType.PERCENTAGE);
            }
            
            CouponDTO createdCoupon = couponService.createCoupon(coupon);
            redirectAttributes.addFlashAttribute("successMessage", "Coupon created successfully");
            
            return "redirect:/coupon-list";
        } catch (Exception e) {
            log.error("Error creating coupon: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating coupon: " + e.getMessage());
            redirectAttributes.addFlashAttribute("coupon", coupon);
            
            return "redirect:/create-coupon";
        }
    }
    
    /**
     * Hiển thị form chỉnh sửa coupon
     */
    @GetMapping("/edit-coupon/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Showing edit form for coupon with ID: {}", id);
        
        try {
            CouponDTO coupon = couponService.findCouponById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid coupon ID: " + id));
                    
            model.addAttribute("coupon", coupon);
            model.addAttribute("discountTypes", Arrays.asList(DiscountType.values()));
            
            return "templates_storage/edit-coupon";
        } catch (Exception e) {
            log.error("Error showing edit form for coupon with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading coupon: " + e.getMessage());
            
            return "redirect:/coupon-list";
        }
    }
    
    /**
     * Xử lý cập nhật coupon
     */
    @PostMapping("/edit-coupon/{id}")
    public String updateCoupon(@PathVariable Long id, @ModelAttribute CouponDTO coupon,
            RedirectAttributes redirectAttributes) {
        log.info("Updating coupon with ID: {}", id);
        
        try {
            CouponDTO updatedCoupon = couponService.updateCoupon(id, coupon);
            redirectAttributes.addFlashAttribute("successMessage", "Coupon updated successfully");
            
            return "redirect:/coupon-list";
        } catch (Exception e) {
            log.error("Error updating coupon with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating coupon: " + e.getMessage());
            redirectAttributes.addFlashAttribute("coupon", coupon);
            
            return "redirect:/edit-coupon/" + id;
        }
    }
    
    /**
     * Xóa coupon
     */
    @PostMapping("/delete-coupon/{id}")
    public String deleteCoupon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Deleting coupon with ID: {}", id);
        
        try {
            couponService.deleteCoupon(id);
            redirectAttributes.addFlashAttribute("successMessage", "Coupon deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting coupon with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting coupon: " + e.getMessage());
        }
        
        return "redirect:/coupon-list";
    }
    
    /**
     * Hiển thị danh sách coupon còn hạn
     */
    @GetMapping("/valid-coupons")
    public String getValidCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            Model model) {
            
        log.info("Fetching valid coupons with page={}, size={}, sortBy={}, sortDir={}", 
                 page, size, sortBy, sortDir);
                 
        Page<CouponDTO> couponPage = couponService.findValidCoupons(page, size, sortBy, sortDir);
        
        model.addAttribute("coupons", couponPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", couponPage.getTotalPages());
        model.addAttribute("totalItems", couponPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("validFilter", true);
        
        return "templates_storage/coupon-list";
    }
    
    /**
     * Hiển thị danh sách coupon hết hạn
     */
    @GetMapping("/expired-coupons")
    public String getExpiredCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            Model model) {
            
        log.info("Fetching expired coupons with page={}, size={}, sortBy={}, sortDir={}", 
                 page, size, sortBy, sortDir);
                 
        Page<CouponDTO> couponPage = couponService.findExpiredCoupons(page, size, sortBy, sortDir);
        
        model.addAttribute("coupons", couponPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", couponPage.getTotalPages());
        model.addAttribute("totalItems", couponPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("expiredFilter", true);
        
        return "templates_storage/coupon-list";
    }

    /**
     * API endpoint for applying a coupon to an order
     */
    @PostMapping("/api/apply-coupon")
    @ResponseBody
    public ResponseEntity<?> applyCoupon(
            @RequestParam String code, 
            @RequestParam(required = false) BigDecimal orderTotal,
            @RequestParam(required = false) Long userId) {
        log.info("Attempting to apply coupon with code: {} to order with total: {}, userId: {}", 
                code, orderTotal, userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<CouponDTO> couponOpt = couponService.findCouponByCode(code);
            
            if (!couponOpt.isPresent()) {
                log.warn("Coupon with code {} not found", code);
                response.put("success", false);
                response.put("message", "Invalid coupon code. Please try again.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            CouponDTO coupon = couponOpt.get();
            
            // Check if coupon is expired
            if (coupon.isExpired()) {
                log.warn("Coupon with code {} has expired", code);
                response.put("success", false);
                response.put("message", "This coupon has expired.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Check if user has already used this coupon
            if (userId != null && userCouponService.hasUserUsedCoupon(userId, coupon.getId())) {
                log.warn("User {} has already used coupon {}", userId, code);
                response.put("success", false);
                response.put("message", "You have already used this coupon.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Check minimum order amount if set
            if (orderTotal != null && coupon.getMinimumOrderAmount() != null && 
                orderTotal.compareTo(coupon.getMinimumOrderAmount()) < 0) {
                log.warn("Order total {} does not meet minimum amount {} for coupon {}", 
                         orderTotal, coupon.getMinimumOrderAmount(), code);
                response.put("success", false);
                response.put("message", "This coupon requires a minimum order of " + 
                            coupon.getMinimumOrderAmount() + ".");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Calculate discount amount
            BigDecimal discountAmount;
            if (orderTotal != null) {
                if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                    // Calculate percentage discount (value is treated as percentage)
                    discountAmount = orderTotal.multiply(coupon.getValue().divide(new BigDecimal(100)));
                } else {
                    // Fixed amount discount
                    discountAmount = coupon.getValue();
                    // Ensure discount doesn't exceed order total
                    if (discountAmount.compareTo(orderTotal) > 0) {
                        discountAmount = orderTotal;
                    }
                }
            } else {
                // If no order total provided, just return the coupon details
                discountAmount = coupon.getValue();
            }
            
            response.put("success", true);
            response.put("message", "Coupon applied successfully!");
            response.put("coupon", coupon);
            response.put("discountAmount", discountAmount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error applying coupon: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while applying the coupon. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 