package org.project.repository;

import java.util.Optional;

import org.project.entity.CouponEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Optional;
import org.project.enums.DiscountType;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {

    /**
     * Tìm kiếm coupon theo mã code
     */
    Optional<CouponEntity> findByCode(String code);

    /**
     * Tìm kiếm coupon với nhiều điều kiện kết hợp
     * Note: Removed LOWER() from description field because it's a CLOB type
     */
    @Query("SELECT c FROM CouponEntity c WHERE (:keyword IS NULL OR LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR c.description LIKE CONCAT('%', :keyword, '%'))")
    Page<CouponEntity> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Tìm kiếm coupon còn hạn sử dụng
     */
    Page<CouponEntity> findByExpirationDateGreaterThanEqual(Date today, Pageable pageable);

    /**
     * Tìm kiếm coupon hết hạn
     */
    Page<CouponEntity> findByExpirationDateLessThan(Date today, Pageable pageable);

    /**
     * Tìm kiếm coupon theo giá trị lớn hơn hoặc bằng
     */
    Page<CouponEntity> findByValueGreaterThanEqual(double value, Pageable pageable);

    /**
     * Tìm kiếm coupon theo loại giảm giá
     * 
     * @param discountType Loại giảm giá cần tìm
     * @param pageable Thông tin phân trang
     * @return Trang dữ liệu các coupon theo loại giảm giá
     */
    Page<CouponEntity> findByDiscountType(DiscountType discountType, Pageable pageable);

    /**
     * Tìm kiếm coupon với nhiều điều kiện kết hợp
     * 
     * @param discountType Loại giảm giá cần tìm (null nếu không lọc)
     * @param validOnly Chỉ lấy coupon còn hạn
     * @param expiredOnly Chỉ lấy coupon hết hạn
     * @param today Ngày hiện tại để so sánh hạn sử dụng
     * @param keyword Từ khóa tìm kiếm (null nếu không tìm)
     * @param pageable Thông tin phân trang
     * @return Trang dữ liệu các coupon theo điều kiện
     */
    @Query("SELECT c FROM CouponEntity c WHERE " +
       "(:discountType IS NULL OR c.discountType = :discountType) AND " +
       "((:validOnly = false AND :expiredOnly = false) OR " +
       "(:validOnly = true AND c.expirationDate >= :today) OR " +
       "(:expiredOnly = true AND c.expirationDate < :today)) AND " +
       "(:keyword IS NULL OR LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "c.description LIKE CONCAT('%', :keyword, '%')) AND " +
       "(:includeInactive = true OR c.status = org.project.enums.CouponStatus.ACTIVE)")
Page<CouponEntity> findWithFilters(
       @Param("discountType") DiscountType discountType,
       @Param("validOnly") boolean validOnly,
       @Param("expiredOnly") boolean expiredOnly,
       @Param("today") Date today,
       @Param("keyword") String keyword,
       @Param("includeInactive") boolean includeInactive,
       Pageable pageable);

    /**
     * Kiểm tra mã code đã tồn tại chưa
     */
    boolean existsByCode(String code);

    void deleteCouponEntityById(Long id);
}