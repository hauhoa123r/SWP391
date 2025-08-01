package org.project.service;

import org.project.model.dto.CouponDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Service interface cho quản lý coupon
 */
public interface CouponService {
    
    /**
     * Tìm tất cả coupon có phân trang
     */
    Page<CouponDTO> findAllCoupons(int page, int size, String sortBy, String sortDir);
    
    /**
     * Tìm coupon theo ID
     */
    Optional<CouponDTO> findCouponById(Long id);
    
    /**
     * Tìm coupon theo mã code
     */
    Optional<CouponDTO> findCouponByCode(String code);
    
    /**
     * Tìm kiếm coupon với nhiều điều kiện kết hợp
     */
    Page<CouponDTO> searchCoupons(String keyword, int page, int size, String sortBy, String sortDir);
    
    /**
     * Tạo coupon mới
     */
    CouponDTO createCoupon(CouponDTO couponDTO);
    
    /**
     * Cập nhật coupon
     */
    CouponDTO updateCoupon(Long id, CouponDTO couponDTO);
    
    /**
     * Xóa coupon
     */
    void deleteCoupon(Long id);
    
    /**
     * Kiểm tra mã coupon đã tồn tại chưa
     */
    boolean isCouponCodeExists(String code);
    
    /**
     * Lấy danh sách coupon còn hạn
     */
    Page<CouponDTO> findValidCoupons(int page, int size, String sortBy, String sortDir);
    
    /**
     * Lấy danh sách coupon hết hạn
     */
    Page<CouponDTO> findExpiredCoupons(int page, int size, String sortBy, String sortDir);
} 