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
     * Kiểm tra mã code đã tồn tại chưa
     */
    boolean existsByCode(String code);
}