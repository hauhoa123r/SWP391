package org.project.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.CouponEntity;
import org.project.enums.DiscountType;
import org.project.exception.CouponException;
import org.project.model.dto.CouponDTO;
import org.project.repository.CouponRepository;
import org.project.service.CouponService;
import org.project.service.UserCouponService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final UserCouponService userCouponService;
    private final CouponRepository couponRepository;
    
    /**
     * Hiển thị danh sách coupon
     */
    @GetMapping("/coupon-list")
    public String getCouponList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            Model model) {

        log.info("Fetching coupons with page={}, size={}, keyword={}, status={}, discountType={}, sortBy={}, sortDir={}",
                page, size, keyword, status, discountType, sortBy, sortDir);

        // Sử dụng phương thức mới để xử lý lọc đa điều kiện
        Page<CouponDTO> couponPage = couponService.findCouponsWithFilters(
                status, discountType, keyword, page, size, sortBy, sortDir);
        
        // Thêm các thuộc tính cho view
        model.addAttribute("coupons", couponPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", couponPage.getTotalPages());
        model.addAttribute("totalItems", couponPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        // Thêm các thuộc tính phục vụ cho form lọc
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("keyword", keyword);
        }
        if ("valid".equals(status)) {
            model.addAttribute("validFilter", true);
        } else if ("expired".equals(status)) {
            model.addAttribute("expiredFilter", true);
        }
        if (discountType != null && !discountType.isEmpty()) {
            model.addAttribute("discountType", discountType);
        }

        // Add current user for forms
        model.addAttribute("currentUser", getCurrentUser());

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
    public String deleteCoupon(@PathVariable Long id, RedirectAttributes redirectAttributes,
                               @RequestParam("code") String code,
                               HttpSession session) {
         log.info("Deleting coupon with ID: {}", id);

        try {
//            // Get userId from session (update this based on your app's auth setup)
//            Long userId = (Long) session.getAttribute("userId");
//            if (userId == null) {
//                redirectAttributes.addFlashAttribute("couponError", "User not logged in.");
//                return "redirect:/login";
//            }
            Long userId=2l;
            couponService.applyCoupon(code, userId, session);
            redirectAttributes.addFlashAttribute("couponSuccess", "Coupon applied successfully!");
        } catch (CouponException e) {
            redirectAttributes.addFlashAttribute("couponError", e.getMessage());
        }

        return "redirect:/cart";
    }
    @GetMapping("/coupon/remove")
    public String removeCoupon(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("appliedCoupon");
        session.removeAttribute("discountedTotal");
        redirectAttributes.addFlashAttribute("couponSuccess", "Coupon removed.");
        return "redirect:/cart";
    }

    @PostMapping("/coupon/apply")
    public String applyCoupon(@RequestParam("code") String code,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
//            // Get userId from session (update this based on your app's auth setup)
//            Long userId = (Long) session.getAttribute("userId");
//            if (userId == null) {
//                redirectAttributes.addFlashAttribute("couponError", "User not logged in.");
//                return "redirect:/login";
//            }
            Long userId=2l;

            couponService.applyCoupon(code, userId, session);
            redirectAttributes.addFlashAttribute("couponSuccess", "Coupon applied successfully!");
        } catch (CouponException e) {
            redirectAttributes.addFlashAttribute("couponError", e.getMessage());
        }

        return "redirect:/cart";
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

//    @PostMapping("/coupon-delete/{id}")
//    public String deleteCoupon(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
//        CouponEntity appliedCoupon = (CouponEntity) session.getAttribute("appliedCoupon");
//        Optional<CouponEntity> couponOptional = couponRepository.findById(id);
//        //Check if coupon with id exists
//        if (!couponRepository.existsById(id)) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Coupon with id " + id + " not found");
//            return "redirect:/coupon-list";
//        }
//        //Check if coupon is being used by another user
//        if (couponOptional.isPresent() && appliedCoupon != null && appliedCoupon.getId().equals(couponOptional.get().getId())) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Coupon with id " + id + " is being used");
//            return "redirect:/coupon-list";
//        }
//        // Delete
//        try {
//            couponService.deleteCoupon(id);
//            redirectAttributes.addFlashAttribute("successMessage", "Coupon with id " + id + " is successfully deleted!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete coupon with id " + id + ": " + e.getMessage());
//            return "redirect:/coupon-list";
//        }
//
//        return "redirect:/coupon-list";
//    }

    @PostMapping("/coupon-delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteCoupon(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        CouponEntity appliedCoupon = (CouponEntity) session.getAttribute("appliedCoupon");
        Optional<CouponEntity> couponOptional = couponRepository.findById(id);
        if (id == null) {
            response.put("success", false);
            response.put("message", "Invalid coupon ID");
            return response;
        }

        if (!couponRepository.existsById(id)) {
            response.put("success", false);
            response.put("message", "Coupon with id " + id + " not found");
            return response;
        }

//        Long appliedCouponId = (Long) session.getAttribute("appliedCoupon");
//        if (appliedCouponId != null && appliedCouponId.equals(id)) {
//            response.put("success", false);
//            response.put("message", "Coupon with id " + id + " is being used");
//            return response;
//        }

        //Check if coupon is being used by another user
        if (couponOptional.isPresent() && appliedCoupon != null && appliedCoupon.getId().equals(couponOptional.get().getId())) {
            response.put("success", false);
            response.put("message", "Coupon with id " + id + " is being used");
            return response;
        }

        try {
            couponService.deleteCoupon(id);
            response.put("success", true);
            response.put("message", "Coupon with id " + id + " is successfully deleted!");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete coupon with id " + id + ": " + e.getMessage());
        }
        return response;
    }




    private Object getCurrentUser() {
        // TODO: Implement proper user authentication
        // For now, return a simple object with required properties
        return new Object() {
            public Long getId() { return 256L; }
            public String getFullName() { return "Người dùng"; }
            public String getRoleName() { return "STAFF"; }
            public String getAvatar() { return "/templates_storage/assets/images/avatar.png"; }
        };
    }
} 