package org.project.converter;

import java.util.Optional;

import org.project.config.ModelMapperConfig;
import org.project.entity.ProductEntity;
import org.project.model.response.PharmacyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConverterPharmacyProduct {

    private ModelMapperConfig modelMapperConfig;

    @Autowired
    public void setConverterPharmacyProduct(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
        this.modelMapperConfig.mapper().typeMap(ProductEntity.class, PharmacyResponse.class).setPostConverter(context -> {
            ProductEntity source = context.getSource();
            PharmacyResponse destination = context.getDestination();
            destination.setRating(calculateRating(source));
            destination.setLabel(source.getLabel() != null ? source.getLabel().name() : null);
            destination.setStatus(source.getProductStatus() != null ? source.getProductStatus().name() : null);
                
            // Đảm bảo stockQuantity được đặt đúng cách
            if (source.getStockQuantities() != null) {
                destination.setStockQuantity(source.getStockQuantities());
            } else {
                destination.setStockQuantity(0); // Default value
            }
            
            // Join all category names by comma for detail page display
            String joinedCategories = source.getCategoryEntities() != null && !source.getCategoryEntities().isEmpty()
                    ? source.getCategoryEntities().stream().map(cat -> cat.getName()).toList().stream().reduce((a,b)->a + ", " + b).orElse(null)
                    : null;
            destination.setCategory(joinedCategories);
            // Set product type for listing pages
            destination.setProductType(source.getProductType() != null ? source.getProductType().name() : null);
            destination.setTags(source.getProductTagEntities() != null
                    ? source.getProductTagEntities().stream().map(tag -> tag.getId().getName()).toList() : null);
            
            return destination;
        });
    }

    public PharmacyResponse toDto(ProductEntity productEntity) {
        if (productEntity == null) return null;
        
        Optional<PharmacyResponse> pharmacyResponseOptional = Optional.ofNullable(modelMapperConfig.mapper().map(productEntity, PharmacyResponse.class));
        PharmacyResponse response = pharmacyResponseOptional.orElse(new PharmacyResponse());
        
        // Đảm bảo stockQuantity được đặt ngay cả khi mapper không thực hiện
        if (response.getStockQuantity() == null && productEntity.getStockQuantities() != null) {
            response.setStockQuantity(productEntity.getStockQuantities());
        }
        return response;
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