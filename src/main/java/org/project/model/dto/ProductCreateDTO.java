package org.project.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.project.enums.Label;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class ProductCreateDTO {
	//product name 
	@NotBlank(message = "Product name is required") 
	private String name; 
	//description 
	private String description; 
	//price 
	@NotNull(message = "Price is required") 
	@DecimalMin(value = "0.0", inclusive = false, 
	message = "Price must be greater than 0")
	private BigDecimal price; 
	//unit 
	@NotBlank(message = "Unit is required") 
	private String unit; 
	//stock quantities 
	@NotNull(message = "Quantities in Stock is required") 
	@Min(value = 0, message = "Stock must be >= 0") 
	private Integer stockQuantities; 
	//image file (multipartFile allow uploads file using java.nio)
	private MultipartFile imageFile; 
	//Product type 
	@NotNull(message = "Product type is required") 
	private ProductType type; 
	//product status 
	@NotNull(message = "Product status is required") 
	private ProductStatus status; 
	//label 
	@NotNull(message = "Label is required") 
	private Label label; 
	//category's ids 
	private Set<Long> categoryIds; 
}
