package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.ProductEntity;
import org.project.entity.ProductTagEntityId;
import org.project.model.response.PharmacyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConverterProduct {
    @Autowired
    private ModelMapperConfig model;

    public List<PharmacyResponse> toConverterProductList(List<ProductEntity> productEntities) {
        return productEntities.stream().map(this::toConverterProduct).collect(Collectors.toList());
    }

    public PharmacyResponse toConverterProduct(ProductEntity productEntity) {
        PharmacyResponse response = model.mapper().map(productEntity, PharmacyResponse.class);
        response.setStatus(productEntity.getProductStatus() != null ? productEntity.getProductStatus().name() : null);
        response.setLabel(productEntity.getLabel() != null ? productEntity.getLabel().name() : null);
        response.setRating(calculateRating(productEntity));
        response.setCategory(productEntity.getCategoryEntities() != null && !productEntity.getCategoryEntities().isEmpty() ? productEntity.getCategoryEntities().iterator().next().getName() : null);
        response.setTags(productEntity.getProductTagEntities() != null
                            ? productEntity.getProductTagEntities().stream()
                                .map(tagEntity -> tagEntity.getId().getName())
                                .collect(Collectors.toList()): null
    );
        return response;
    }

    private Double calculateRating(ProductEntity productEntity) {
        if (productEntity.getReviewEntities() == null || productEntity.getReviewEntities().isEmpty()) {
            return 0.0;
        }
        return productEntity.getReviewEntities().stream().mapToDouble(review -> review.getRating() != null ? review.getRating().doubleValue() : 0.0).average().orElse(0.0);
    }
}