package org.project.converter;

import org.project.entity.ProductEntity;
import org.project.model.response.PharmacyListResponse;
import org.springframework.stereotype.Component;

@Component
public class ConverterPharmacyProduct {
    public PharmacyListResponse toDto(ProductEntity entity) {
        PharmacyListResponse dto = new PharmacyListResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setRating(calculateRating(entity));
        dto.setLabel(entity.getLabel() != null ? entity.getLabel().name() : null);
        dto.setStatus(entity.getProductStatus() != null ? entity.getProductStatus().name() : null);
        dto.setStockQuantity(entity.getStockQuantities());
        dto.setCategory(entity.getCategoryEntities() != null && !entity.getCategoryEntities().isEmpty()
                ? entity.getCategoryEntities().iterator().next().getName() : null);
        dto.setTags(entity.getProductTagEntities() != null
                ? entity.getProductTagEntities().stream().map(tag -> tag.getId().getName()).toList() : null);
        dto.setImageUrl(entity.getImageUrl());
        return dto;
    }

    private double calculateRating(ProductEntity entity) {
        return entity.getReviewEntities() != null && !entity.getReviewEntities().isEmpty()
                ? entity.getReviewEntities().stream()
                .mapToDouble(review -> review.getRating()) // Giả sử ReviewEntity có getRating()
                .average()
                .orElse(0.0)
                : 0.0;
    }
}