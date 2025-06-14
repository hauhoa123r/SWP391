package org.project.repository.impl.custom;

import org.project.entity.ProductEntity;
import org.project.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductEntity> findAllByProductType(ProductType productType, Pageable pageable);
}
