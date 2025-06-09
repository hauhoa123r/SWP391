package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.ProductEntity;
import org.project.model.response.PharmacyListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConverterPharmacyProduct {
    @Autowired
    private ModelMapperConfig model;

    public List<PharmacyListResponse> toConverterPharmacyProductList(List<ProductEntity> pharmacyProductEntities) {
        return pharmacyProductEntities.stream()
                .map(pharmacyProductEntity -> {
                    PharmacyListResponse response = model.mapper().map(pharmacyProductEntity, PharmacyListResponse.class);
                    response.setCategory(pharmacyProductEntity.getCategory() != null ? pharmacyProductEntity.getCategory().getName() : null);
                    return response;
                })
                .toList();
    }
}
