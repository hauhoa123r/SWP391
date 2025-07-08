package org.project.converter;

import org.project.config.ModelMapperConfig; 
import org.project.entity.ProductEntity;
import org.project.enums.Label;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
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
