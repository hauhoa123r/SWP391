package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.ProductEntity;
import org.project.entity.ReviewEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.response.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductConverter {

    private ModelMapperConfig modelMapperConfig;

    @Autowired
    public void setModelMapperConfig(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
        this.modelMapperConfig.mapper().typeMap(ProductEntity.class, ProductResponse.class).setPostConverter(context -> {
            ProductEntity productEntity = context.getSource();
            ProductResponse productResponse = context.getDestination();
            
            productResponse.setReviewCount(productEntity.getReviewEntities().size());
            productResponse.setAverageRating(productEntity.getReviewEntities().stream().mapToDouble(ReviewEntity::getRating).average().orElse(0.0));

            return productResponse;
        });
    }

    public ProductResponse toResponse(ProductEntity productEntity) {
        Optional<ProductResponse> productRespsonseOptional = Optional.ofNullable(modelMapperConfig.mapper().map(productEntity, ProductResponse.class));
        return productRespsonseOptional.orElseThrow(() -> new ErrorMappingException(ProductEntity.class, ProductResponse.class));
    }
}
