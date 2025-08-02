package org.project.service;

import jakarta.servlet.http.HttpSession;
import org.project.exception.CouponException;
import java.math.BigDecimal;
import org.project.model.dto.CouponDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import org.project.exception.CouponException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import org.project.enums.DiscountType;

@Service
/**
 * Service interface cho quản lý coupon
 */
public interface CouponService {
    BigDecimal applyCoupon(String code, Long userId, HttpSession session) throws CouponException;


    BigDecimal applyCouponToCart(String code, Long userId, HttpSession session) throws CouponException;

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

    /**
     * Tìm kiếm coupon theo loại giảm giá
     * 
     * @param discountType Loại giảm giá cần tìm
     * @param page Số trang
     * @param size Kích thước trang
     * @param sortBy Trường sắp xếp
     * @param sortDir Hướng sắp xếp
     * @return Trang dữ liệu các coupon theo loại giảm giá
     */
    Page<CouponDTO> findCouponsByDiscountType(DiscountType discountType, int page, int size, String sortBy, String sortDir);

    /**
     * Tìm coupon với nhiều điều kiện lọc kết hợp
     * 
     * @param status "valid", "expired", hoặc null để lọc theo trạng thái hạn sử dụng
     * @param discountTypeStr String tên của DiscountType hoặc null
     * @param keyword Từ khóa tìm kiếm hoặc null
     * @param page Số trang
     * @param size Kích thước trang
     * @param sortBy Trường sắp xếp
     * @param sortDir Hướng sắp xếp
     * @return Trang dữ liệu các coupon theo điều kiện lọc
     */
    Page<CouponDTO> findCouponsWithFilters(String status, String discountTypeStr, String keyword, 
                                           int page, int size, String sortBy, String sortDir);

    //delete coupon from the list of coupon by its id
}