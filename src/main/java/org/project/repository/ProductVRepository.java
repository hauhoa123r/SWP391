package org.project.repository;

import org.project.entity.ProductEntity;
import org.project.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    List<ProductEntity> findByProductType(ProductType productType);
    Page<ProductEntity> findByProductTypeAndNameContainingIgnoreCase(ProductType productType, String name, Pageable pageable);
}
