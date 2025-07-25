package org.project.model.dto;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.project.model.response.CategoryResponse;
import org.project.model.response.ProductAdditionalInfoResponse;

import lombok.Data;

@Data
public class ProductDetailDTO {
	private ProductViewDTO product; 
	private Set<String> categories; 
	private Set<String> tags; 
	private Map<String, String> additionalInfos; 
	private List<ReviewDTO> reviews; 
	private Integer reviewCount;
    private Double averageRating; 
}
