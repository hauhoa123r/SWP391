package org.project.service;

import org.project.model.dto.UserCouponDTO;

import java.util.List;

/**
 * Service interface for managing user-coupon relationships
 */
public interface UserCouponService {
    
    /**
     * Record a coupon usage by a user
     */
    UserCouponDTO recordCouponUsage(Long userId, Long couponId);
    
    /**
     * Check if a user has already used a coupon
     */
    boolean hasUserUsedCoupon(Long userId, Long couponId);
    
    /**
     * Find all coupons used by a specific user
     */
    List<UserCouponDTO> findCouponsByUserId(Long userId);
    
    /**
     * Find all users who have used a specific coupon
     */
    List<UserCouponDTO> findUsersByCouponId(Long couponId);
    
    /**
     * Get the number of times a coupon has been used
     */
    int getCouponUsageCount(Long couponId);
} 