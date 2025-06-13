package org.project.repository;

import java.util.Optional;

import org.project.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long>{
	Optional<CouponEntity> findByCode(String code);
}
