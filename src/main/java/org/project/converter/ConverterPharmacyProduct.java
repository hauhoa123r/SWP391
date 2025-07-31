package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.ProductEntity;
import org.project.enums.Label;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.project.model.dto.ProductViewDTO;
import org.project.model.response.PharmacyListResponse;
import org.project.model.response.PharmacyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    //convert Entity <-> Response (based on list)
    public List<PharmacyListResponse> toConverterPharmacyProductList(List<ProductEntity> pharmacyProductEntities) {
        return pharmacyProductEntities.stream()
                .map(pharmacyProductEntity -> modelMapperConfig.mapper().map(pharmacyProductEntity, PharmacyListResponse.class))
                .toList();
    }

    //Convert entity to response
    public PharmacyListResponse toConverterPharmacyResponse(ProductEntity entity) {
        return modelMapperConfig.mapper().map(entity, PharmacyListResponse.class);
    }

    //Convert Object[] to ProductViewDTO
    public ProductViewDTO toConverterProductViewDTO(Object[] product) {
        ProductViewDTO productViewDTO = new ProductViewDTO();
        // Map the fields from the Object[] to ProductViewDTO
        //Mapping id
        Long id = ((Number) product[0]).longValue();
        //Mapping type
        ProductType type = ProductType.valueOf((String) product[1]);
        //Mapping name
        String name = (String) product[2];
        //Mapping description
        String description = (String) product[3];
        //Mapping price
        BigDecimal price = ((BigDecimal) product[4]);
        //Mapping unit
        String unit = (String) product[5];
        //Mapping ProductStatus
        ProductStatus productStatus = ProductStatus.valueOf((String) product[6]);
        //Mapping StockQuantities
        Integer stockQuantities = ((Number) product[7]).intValue();
        //Mapping imageUrl
        String imageUrl = (String) product[8];
        //Mapping label
        Label label = Label.valueOf((String) product[9]);
        //mapping categoryNames
        String categoryNames = (String) product[10];
        //Mapping tagNames
        String tagNames = (String) product[11];
        //Mapping additinalInfos
        String additionalInfos = (String) product[12];

        //set values to ProductViewDTO
        productViewDTO.setId(id);
        //set type
        productViewDTO.setType(type);
        //set name
        productViewDTO.setName(name);
        //set description
        productViewDTO.setDescription(description);
        productViewDTO.setPrice(price);
        //set unit
        productViewDTO.setUnit(unit);
        //set productStatus
        productViewDTO.setProductStatus(productStatus);
        //set stockQuantities
        productViewDTO.setStockQuantities(stockQuantities);
        //set imageUrl
        productViewDTO.setImageUrl(imageUrl);
        //set label
        productViewDTO.setLabel(label);
        //set categoryNames
        productViewDTO.setCategoryNames(categoryNames);
        //set tagNames
        productViewDTO.setTagNames(tagNames);
        //set additionalInfos
        productViewDTO.setAdditionalInfos(additionalInfos);

        //Mapping
        return productViewDTO;
    }
}