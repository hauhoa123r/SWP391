package org.project.converter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.project.entity.ProductAdditionalInfoEntity;
import org.project.entity.ProductEntity;
import org.project.entity.ProductTagEntity;
import org.project.entity.ProductTagEntityId;
import org.project.model.dto.ProductUpdateDTO;
import org.project.model.response.ProductAdditionalInfoResponse;
import org.project.repository.CategoryRepository;
import org.project.repository.ProductAdditionalInfoRepository;
import org.project.repository.ProductTagRepository;
import org.project.service.FileStorageService;
import org.project.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class ProductUpdateConverter {

	// inject storage service
	@Autowired
	private FileStorageService fileStorageService;

	// inject categoryRepo
	@Autowired
	private CategoryRepository categoryRepo;

	// inject tagRepo
	@Autowired
	private ProductTagRepository tagRepo;
	//inject additionalInfoRepo
	@Autowired
	private ProductAdditionalInfoRepository productAdditionalInfoRepo;

	// Convert from dto to Entity
	public ProductEntity convertToEntity(ProductUpdateDTO dto) {
		// new entity
		ProductEntity entity = new ProductEntity();
		//set id 
		entity.setId(dto.getId()); 
		// set name
		entity.setName(dto.getName());
		// set đescription
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
			entity.setImageUrl(imageUrl);
		} else {
			entity.setImageUrl(dto.getCurrentImageUrl());
		}
		// set category
		if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
			// set categories based on categories by id 
			entity.setCategoryEntities(dto.getCategoryIds().stream()
					.map(id -> categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("Category not found")))
					.collect(Collectors.toSet()));
		}
		// set tags using method 
		entity.setProductTagEntities(convertTagsToEntities(entity, dto.getTags())); 
		//set additional infos using method 
		entity.setProductAdditionalInfoEntities(convertAdditionalInfosToEntities(entity, dto.getAdditionalInfos())); 
		//return entity 
		return entity; 
	} 
	
	//convert from entity to dto  
	public ProductUpdateDTO convertToDTO(ProductEntity entity) {
		//new dto 
		ProductUpdateDTO dto = new ProductUpdateDTO(); 
		//set id 
		dto.setId(entity.getId()); 
		//set name 
		dto.setName(entity.getName()); 
		//set description 
		dto.setDescription(entity.getDescription()); 
		//set price 
		dto.setPrice(entity.getPrice()); 
		//set unit 
		dto.setUnit(entity.getUnit()); 
		//set stock quantities 
		dto.setStockQuantities(entity.getStockQuantities()); 
		//set product type 
		dto.setType(entity.getProductType()); 
		//set status 
		dto.setStatus(entity.getProductStatus()); 
		//set label 
		dto.setLabel(entity.getLabel()); 
		//Check if image url is not null 
		if (entity.getImageUrl() != null) {
			dto.setCurrentImageUrl(entity.getImageUrl());
		} 
		
		//set category ids 
		if (entity.getCategoryEntities() != null && !entity.getCategoryEntities().isEmpty()) {
			dto.setCategoryIds(entity.getCategoryEntities().stream()
					.map(category -> category.getId()).collect(Collectors.toSet()));
			} else { 
				dto.setCategoryIds(Set.of()); // set empty set if no categories 
			}
		//set tags 
		if (entity.getProductTagEntities() != null && !entity.getProductTagEntities().isEmpty()) {
			dto.setTags(entity.getProductTagEntities().stream()
					.map(tag -> tag.getId().getName()).collect(Collectors.toSet()));
		} else {
			dto.setTags(Set.of()); // set empty set if no tags 
		}
		//set additional infos 
		if (entity.getProductAdditionalInfoEntities() != null && !entity.getProductAdditionalInfoEntities().isEmpty()) {
			dto.setAdditionalInfos(entity.getProductAdditionalInfoEntities().stream()
					.map(info -> new ProductAdditionalInfoResponse(info.getId(),info.getName(), info.getValue()))
					.collect(Collectors.toList()));
		} else {
			dto.setAdditionalInfos(List.of()); // set empty list if no additional infos 
		}
		//return dto 
		return dto; 
	}
	
//	//nethod to convert tags from dto to entity
//	public Set<ProductTagEntity> convertTagsToEntities(ProductEntity product, Set<String> tags) {
//		// Check if tags are not null
//		if (tags == null || tags.isEmpty()) {
//			return Set.of(); // return empty set if no tags
//		}
//		// Convert tags to entities
//		return tags.stream().map(tagName -> {
//			ProductTagEntity tag = new ProductTagEntity();
//			tag.setId(new ProductTagEntityId(product.getId(), tagName));
//			tag.setProductEntity(product);
//			return tag;
//		}).collect(Collectors.toSet());
//	}

	//nethod to convert tags from dto to entity
	public Set<ProductTagEntity> convertTagsToEntities(ProductEntity product, Set<String> tags) {
		// Check if tags are not null
		if (tags == null || tags.isEmpty()) {
			return Set.of(); // return empty set if no tags
		}
		// Convert tags to entities
		return tags.stream().map(tagName -> {
			ProductTagEntityId id = new ProductTagEntityId(product.getId(), tagName);
			return tagRepo.findById(id).orElseGet(() -> {
				ProductTagEntity tag = new ProductTagEntity();
				tag.setId(id);
				tag.setProductEntity(product);
				//save
				tagRepo.save(tag);
				return tag;
			});
		}).collect(Collectors.toSet());
	}
//public Set<ProductTagEntity> convertTagsToEntities(ProductEntity product, Set<String> tags) {
//	if (tags == null || tags.isEmpty()) {
//		return Set.of();
//	}
//
//	return tags.stream().map(tagName -> {
//		ProductTagEntityId id = new ProductTagEntityId(product.getId(), tagName);
//
//		// Try find from DB
//		Optional<ProductTagEntity> optionalTag = tagRepo.findTagByIdCustom(product.getId(), tagName);
//		if (optionalTag.isPresent()) {
//			return optionalTag.get();
//		}
//
////		// Else create new
////		ProductTagEntity tag = new ProductTagEntity();
////		tag.setId(id);
////		tag.setProductEntity(product);
////		return tag;
//		ProductTagEntity tag = new ProductTagEntity();
//		tag.setProductEntity(product);
//
//// Tạo mới ID sau khi productEntity đã có giá trị
//		ProductTagEntityId newId = new ProductTagEntityId();
//		newId.setProductId(product.getId());
//		newId.setName(tagName);
//		tag.setId(newId);
//
//		return tag;
//	}).collect(Collectors.toSet());
//}
	
	//method to convert additional infos from dto to entity 
	public Set<ProductAdditionalInfoEntity> convertAdditionalInfosToEntities(ProductEntity product, List<ProductAdditionalInfoResponse> additionalInfos) {
		// Check if additional infos are not null 
		if (additionalInfos == null || additionalInfos.isEmpty()) {
			return Set.of(); // return empty set if no additional infos 
		}
		// Convert additional infos to entities
		return additionalInfos.stream().filter(
				//filter and take only additional info with name and value
				info -> info.getName() != null && !info.getName().trim().isEmpty() &&
						info.getValue() != null && !info.getValue().trim().isEmpty()
		).map(info -> {
			ProductAdditionalInfoEntity newInfo = new ProductAdditionalInfoEntity();
			newInfo.setProductEntity(product);
			newInfo.setName(info.getName());
			newInfo.setValue(info.getValue());
//			productAdditionalInfoRepo.save(newInfo);
			return newInfo;
		}).collect(Collectors.toSet());
	} 
}
