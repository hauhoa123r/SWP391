package org.project.converter;

import java.util.Set;
import java.util.stream.Collectors;

import org.project.entity.CategoryEntity;
import org.project.entity.ProductEntity;
import org.project.model.dto.ProductCreateDTO;
import org.project.repository.CategoryRepository;
import org.project.repository.ProductTagRepository;
import org.project.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductCreateConverter {
	// inject storage service
	@Autowired
	private FileStorageService fileStorageService;

	// inject categoryRepo
	@Autowired
	private CategoryRepository categoryRepo;

	// inject tagRepo
	@Autowired
	private ProductTagRepository tagRepo;

	// convert from dto to entity
	public ProductEntity convertToEntity(ProductCreateDTO dto) {
		// new entity
		ProductEntity entity = new ProductEntity();
		// set name
		entity.setName(dto.getName());
		// set description
		entity.setDescription(dto.getDescription());
		// set price
		entity.setPrice(dto.getPrice());
		// set unit
		entity.setUnit(dto.getUnit());
		// set stock quantities
		entity.setStockQuantities(dto.getStockQuantities());
		// set product type
		entity.setProductType(dto.getType());
		// set status
		entity.setProductStatus(dto.getStatus());
		// set label
		entity.setLabel(dto.getLabel());

		// save image and get image url
		if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
			String imageUrl = fileStorageService.saveImage(dto.getImageFile());
			// set url to entity
			entity.setImageUrl(imageUrl);
		}
		// set category
		if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
			// set of categories based on id
			Set<CategoryEntity> categories = dto.getCategoryIds().stream()
					.map(id -> categoryRepo.findById(id)
							.orElseThrow(() -> new RuntimeException("No category found with id :")))
					.collect(Collectors.toSet());
			//set category entities 
			entity.setCategoryEntities(categories); 
		}
		return entity; 
	}
}
