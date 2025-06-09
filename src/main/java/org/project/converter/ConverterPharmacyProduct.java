package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.PharmacyProductEntity;
import org.project.entity.ProductEntity;
import org.project.model.response.PharmacyListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConverterPharmacyProduct {
    @Autowired
    private ModelMapperConfig model;
    
    //convert Entity <-> Response (based on list)
    public List<PharmacyListResponse> toConverterPharmacyProductList(List<ProductEntity> pharmacyProductEntities) {
        return pharmacyProductEntities.stream()
                .map(pharmacyProductEntity -> model.mapper().map(pharmacyProductEntity, PharmacyListResponse.class))
                .toList();
    }
    
    //Convert entity to response 
    public PharmacyListResponse toConverterPharmacyResponse(ProductEntity entity) {
    	return model.mapper().map(entity, PharmacyListResponse.class); 
    }
}
