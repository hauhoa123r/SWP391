package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.ProductAdditionalInfoEntity;
import org.project.repository.ProductAdditionalInfoRepository;
import org.project.service.ProductAdditionalInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAdditionalInfoServiceImpl implements ProductAdditionalInfoService {

    private final ProductAdditionalInfoRepository repository;

    @Override
    public List<ProductAdditionalInfoEntity> findByProductId(Long productId) {
        return repository.findByProductEntityId(productId);
    }
}
