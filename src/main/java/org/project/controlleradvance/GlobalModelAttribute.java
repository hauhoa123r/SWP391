package org.project.controlleradvance;

import org.project.entity.CartItemEntity;
import org.project.entity.NotificationEntity;
import org.project.model.response.NotificationResponse;
import org.project.service.CartService;
import org.project.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalModelAttribute {
    
    private final NotificationService notificationService;
    private final CartService cartService;

    @Autowired
    public GlobalModelAttribute(NotificationService notificationService, CartService cartService) {
        this.notificationService = notificationService;
        this.cartService = cartService;
    }

    /**
     * Lấy thông tin notifications cho header (giới hạn 5 notifications mới nhất)
     */
    @ModelAttribute(name = "notifications")
    public List<NotificationResponse> getNotifications() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            List<NotificationResponse> allNotifications = notificationService.getNotificationsByUser(userId);
            // Giới hạn 5 notifications mới nhất để tối ưu performance
            return allNotifications.stream().limit(5).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Lấy số lượng notifications (không phân biệt đã đọc/chưa đọc)
     */
    @ModelAttribute(name = "notificationCount")
    public Long getNotificationCount() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            return notificationService.getNotificationCount(userId);
        }
        return 0L;
    }

    /**
     * Lấy thông tin cart items cho header (giới hạn 4 items mới nhất)
     */
    @ModelAttribute(name = "headerCartItems")
    public List<CartItemEntity> getHeaderCartItems() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            List<CartItemEntity> cartItems = cartService.getCart(userId);
            // Đảo ngược để mới nhất lên đầu và giới hạn 4 items
            Collections.reverse(cartItems);
            return cartItems.stream().limit(4).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Lấy tổng số items trong cart
     */
    @ModelAttribute(name = "cartItemCount")
    public Integer getCartItemCount() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            List<CartItemEntity> cartItems = cartService.getCart(userId);
            return cartItems.size();
        }
        return 0;
    }

    /**
     * Lấy tổng tiền cart
     */
    @ModelAttribute(name = "cartTotal")
    public java.math.BigDecimal getCartTotal() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            return cartService.calculateTotal(userId);
        }
        return java.math.BigDecimal.ZERO;
    }

    /**
     * Lấy user ID từ Spring Security context
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            
            // Nếu sử dụng AccountDetails
            if (authentication.getPrincipal() instanceof org.project.security.AccountDetails) {
                org.project.security.AccountDetails accountDetails = 
                    (org.project.security.AccountDetails) authentication.getPrincipal();
                return accountDetails.getUserEntity().getId();
            }
            
            // Fallback: nếu có user ID trong principal
            if (authentication.getPrincipal() instanceof org.project.entity.UserEntity) {
                return ((org.project.entity.UserEntity) authentication.getPrincipal()).getId();
            }
        }
        
        // Fallback cho trường hợp chưa đăng nhập hoặc test
        return 2L; // Default user ID cho test
    }
} 