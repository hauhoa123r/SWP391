package org.project.service;

import org.project.entity.ProductAdditionalInfoEntity;
import org.project.model.dto.ProductAdditionalInfoDTO;

import java.util.List;

public interface ProductAdditionalInfoService {
    List<ProductAdditionalInfoEntity> findByProductId(Long productId);

    ProductAdditionalInfoEntity save(ProductAdditionalInfoDTO productAdditionalInfoDTO);
}
