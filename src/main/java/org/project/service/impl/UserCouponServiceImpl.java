package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.CouponEntity;
import org.project.entity.UserCouponEntity;
import org.project.entity.UserCouponEntityId;
import org.project.entity.UserEntity;
import org.project.model.dto.UserCouponDTO;
import org.project.repository.CouponRepository;
import org.project.repository.UserCouponRepository;
import org.project.repository.UserRepository;
import org.project.service.UserCouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public UserCouponDTO recordCouponUsage(Long userId, Long couponId) {
        log.info("Recording coupon usage: userId={}, couponId={}", userId, couponId);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
                
        CouponEntity coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found with ID: " + couponId));
        
        // Create composite ID
        UserCouponEntityId id = new UserCouponEntityId();
        id.setUserId(userId);
        id.setCouponId(couponId);
        
        // Check if this user has already used this coupon
        if (userCouponRepository.existsById(id)) {
            throw new IllegalStateException("User has already used this coupon");
        }
        
        // Create and save the user-coupon relationship
        UserCouponEntity userCoupon = new UserCouponEntity();
        userCoupon.setId(id);
        userCoupon.setUserEntity(user);
        userCoupon.setCouponEntity(coupon);
        userCoupon.setUseAt(Timestamp.from(Instant.now()));
        
        UserCouponEntity savedEntity = userCouponRepository.save(userCoupon);
        return convertToDTO(savedEntity);
    }

    @Override
    public boolean hasUserUsedCoupon(Long userId, Long couponId) {
        return userCouponRepository.existsByUserEntityIdAndCouponEntityId(userId, couponId);
    }

    @Override
    public List<UserCouponDTO> findCouponsByUserId(Long userId) {
        List<UserCouponEntity> userCoupons = userCouponRepository.findByUserEntityId(userId);
        return userCoupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserCouponDTO> findUsersByCouponId(Long couponId) {
        List<UserCouponEntity> userCoupons = userCouponRepository.findByCouponEntityId(couponId);
        return userCoupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public int getCouponUsageCount(Long couponId) {
        return userCouponRepository.countUsagesByCouponId(couponId);
    }
    
    private UserCouponDTO convertToDTO(UserCouponEntity entity) {
        if (entity == null) {
            return null;
        }
        
        UserCouponDTO dto = new UserCouponDTO();
        dto.setUserId(entity.getUserEntity().getId());
        dto.setCouponId(entity.getCouponEntity().getId());
        dto.setCouponCode(entity.getCouponEntity().getCode());
        dto.setUsername(entity.getUserEntity().getEmail()); // Use email instead of username
        dto.setUseAt(entity.getUseAt());
        
        return dto;
    }
} 