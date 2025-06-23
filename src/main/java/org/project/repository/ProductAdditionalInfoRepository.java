package org.project.repository;

import org.project.entity.ProductAdditionalInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductAdditionalInfoRepository extends JpaRepository<ProductAdditionalInfoEntity, Long> {
    List<ProductAdditionalInfoEntity> findByProductEntityId(Long productId);
}
