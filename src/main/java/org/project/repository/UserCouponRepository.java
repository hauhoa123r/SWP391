package org.project.repository;

import org.project.entity.UserCouponEntity;
import org.project.entity.UserCouponEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCouponEntity, UserCouponEntityId> {

    /**
     * Tìm tất cả các coupon mà người dùng đã sử dụng
     */
    List<UserCouponEntity> findByUserEntityId(Long userId);

    /**
     * Tìm tất cả người dùng đã sử dụng coupon
     */
    List<UserCouponEntity> findByCouponEntityId(Long couponId);

    /**
     * Kiểm tra người dùng đã sử dụng coupon chưa
     */
    boolean existsByUserEntityIdAndCouponEntityId(Long userId, Long couponId);

    /**
     * Đếm số lần coupon đã được sử dụng
     */
    @Query("SELECT COUNT(uc) FROM UserCouponEntity uc WHERE uc.couponEntity.id = :couponId")
    int countUsagesByCouponId(@Param("couponId") Long couponId);
} 