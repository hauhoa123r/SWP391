package org.project.admin.repository;

import org.project.admin.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("adminCouponRepository")
public interface CouponRepository extends JpaRepository<Coupon, Long>, JpaSpecificationExecutor<Coupon> {

    @Query(value = "SELECT * FROM coupons WHERE coupon_id = :id", nativeQuery = true)
    Optional<Coupon> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM coupons WHERE deleted = true", nativeQuery = true)
    Page<Coupon> findAllDeleted(Pageable pageable);

}

