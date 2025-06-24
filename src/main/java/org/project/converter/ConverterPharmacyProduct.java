package org.project.converter;

import org.project.config.ModelMapperConfig; 
import org.project.entity.ProductEntity;
import org.project.enums.Label;
import org.project.model.dto.ProductViewDTO;
import org.project.model.response.PharmacyListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    
    //Convert Object[] to ProductViewDTO 
    public ProductViewDTO toConverterProductViewDTO(Object[] product) {
		ProductViewDTO productViewDTO = new ProductViewDTO();
		// Map the fields from the Object[] to ProductViewDTO 
		//Mapping id 
		Long id = ((Number) product[0]).longValue();
		//Mapping name 
		String name = (String) product[1]; 
		//Mapping categoryNames 
		String categoryNames = (String) product[2]; 
		//Mapping price 
		Double price = ((BigDecimal) product[3]).doubleValue();  
		//Mapping stockQuantities 
		Integer stockQuantities = ((Number) product[4]).intValue(); 
		//Mapping label 
		Label label = Label.valueOf((String) product[5]); 
		// Set the fields in ProductViewDTO 
		productViewDTO.setId(id); 
		productViewDTO.setName(name); 
		productViewDTO.setCategoryNames(categoryNames); 
		productViewDTO.setPrice(price); 
		productViewDTO.setStockQuantities(stockQuantities); 
		productViewDTO.setLabel(label); 
		// Return the populated ProductViewDTO 
		return productViewDTO;
	} 
}
