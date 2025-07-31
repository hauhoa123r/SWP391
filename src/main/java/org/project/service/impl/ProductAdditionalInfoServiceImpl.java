package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.converter.ProductAdditionalInfoConverter;
import org.project.entity.ProductAdditionalInfoEntity;
import org.project.model.dto.ProductAdditionalInfoDTO;
import org.project.repository.ProductAdditionalInfoRepository;
import org.project.service.ProductAdditionalInfoService;
import org.project.utils.MergeObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductAdditionalInfoServiceImpl implements ProductAdditionalInfoService {

    private final ProductAdditionalInfoRepository repository;
    private final ProductAdditionalInfoConverter converter;

    @Override
    public List<ProductAdditionalInfoEntity> findByProductId(Long productId) {
        return repository.findByProductEntityId(productId);
    }

    @Override
    public ProductAdditionalInfoEntity save(ProductAdditionalInfoDTO productAdditionalInfoDTO) {
        if (productAdditionalInfoDTO == null) {
            return null;
        }

        if (productAdditionalInfoDTO.getId() != null && repository.existsById(productAdditionalInfoDTO.getId())) {
            Optional<ProductAdditionalInfoEntity> optional = repository.findById(productAdditionalInfoDTO.getId());
            if (optional.isEmpty()) {
                return null; // or throw an exception if preferred
            }
            ProductAdditionalInfoEntity target = optional.get();
            ProductAdditionalInfoEntity source = converter.toEntity(productAdditionalInfoDTO);
            MergeObjectUtils.mergeNonNullFields(source, target);

            return repository.save(target);
        }
        ProductAdditionalInfoEntity productAdditionalInfoEntity = converter.toEntity(productAdditionalInfoDTO);
        return repository.save(productAdditionalInfoEntity);
    }
}
