package org.project.service;

import org.project.entity.ProductAdditionalInfoEntity;

import java.util.List;

public interface ProductAdditionalInfoService {
    List<ProductAdditionalInfoEntity> findByProductId(Long productId);
}
