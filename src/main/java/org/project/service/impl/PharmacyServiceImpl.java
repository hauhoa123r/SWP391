package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.converter.ConverterPharmacyProduct;
import org.project.converter.ProductConverter;
import org.project.converter.ProductCreateConverter;
import org.project.converter.ProductUpdateConverter;
import org.project.entity.CategoryEntity;
import org.project.entity.ProductAdditionalInfoEntity;
import org.project.entity.ProductEntity;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.ProductCreateDTO;
import org.project.model.dto.ProductDetailDTO;
import org.project.model.dto.ProductUpdateDTO;
import org.project.model.dto.ProductViewDTO;
import org.project.model.dto.ReviewDTO;
import org.project.model.response.CategoryResponse;
import org.project.model.response.PharmacyListResponse;
import org.project.model.response.ProductAdditionalInfoResponse;
import org.project.model.response.ProductResponse;
import org.project.projection.ProductViewProjection;
import org.project.repository.*;
import org.project.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PharmacyServiceImpl implements PharmacyService {

	private final AppointmentRepository appointmentRepository;
	@Autowired
	private PharmacyRepository pharmacyRepositoryImpl;

	// Inject productRepo
	@Autowired
	private ProductRepository productRepo;

	// Inject categoryRepo
	@Autowired
	private CategoryRepository categoryRepo;
	// inject productTagRepo
	@Autowired
	private ProductTagRepository tagRepo;

	// inject additionalInfoRepo
	@Autowired
	private ProductAdditionalInfoRepository additionalInfoRepo;

	// inject review repo
	@Autowired
	private ProductReviewRepository reviewRepo;

	// inject converter
	@Autowired
	private ProductCreateConverter productCreateConverter;

	// inject product update converter
	@Autowired
	private ProductUpdateConverter productUpdateConverter;

	@Autowired
	private ConverterPharmacyProduct toConverterPharmacy;

	PharmacyServiceImpl(AppointmentRepository appointmentRepository) {
		this.appointmentRepository = appointmentRepository;
	}

	@Override
	public List<PharmacyListResponse> getAllPharmacies() {
		// list contains entity with full info
		List<ProductEntity> pharmacyProductEntity = pharmacyRepositoryImpl.findAll();
		// convert to list of entities with necessary infos
		List<PharmacyListResponse> pharmacyListResponse = toConverterPharmacy
				.toConverterPharmacyProductList(pharmacyProductEntity);
		return pharmacyListResponse;
	}

	@Override
	public ProductEntity save(ProductEntity pharmacy) {
		// TODO Auto-generated method stub
		// check if pharmacy is null
		if (pharmacy == null) {
			throw new NullPointerException();
		}
		// save
		ProductEntity entity = pharmacyRepositoryImpl.save(pharmacy);
		// return
		return entity;
	}

	@Override
	public PharmacyListResponse findById(Long id) {
		// TODO Auto-generated method stub
		Optional<ProductEntity> pharmacy = pharmacyRepositoryImpl.findById(id);
		// Check
		if (pharmacy.isPresent()) {
			return toConverterPharmacy.toConverterPharmacyResponse(pharmacy.get());
		} else {
			throw new IllegalArgumentException("No pharmacy product found with Id " + id);
		}
	}

	@Override
	public List<PharmacyListResponse> findByProductType(ProductType type) {
		// TODO Auto-generated method stub
		// Get the list of type
		List<ProductEntity> pharmacyList = new ArrayList<>();
		// search
		pharmacyList = pharmacyRepositoryImpl.findByProductTypeContaining(type);
		// Check
		if (pharmacyList != null) {
			return toConverterPharmacy.toConverterPharmacyProductList(pharmacyList);
		}
		return new ArrayList<>();
	}

	@Override
	public List<PharmacyListResponse> findByName(String name) {
		// TODO Auto-generated method stub
		// Get the list of entity has the name
		List<ProductEntity> pharmacyList = new ArrayList<>();
		// search
		pharmacyList = pharmacyRepositoryImpl.findByNameContaining(name);
		// Check
		if (pharmacyList != null) {
			// Convert to response list
			return toConverterPharmacy.toConverterPharmacyProductList(pharmacyList);
		}
		// If not found return empty list
		return new ArrayList<PharmacyListResponse>();
	}

	@Transactional
	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		pharmacyRepositoryImpl.deleteById(id);
		// find by id
		Optional<ProductEntity> entity = pharmacyRepositoryImpl.findById(id);
		// Check if entity is present
		if (entity.isPresent()) {
			System.out.println("Delete unsucessfully");
		}
		System.out.println("Delete successfully");
	}

	@Override
	public List<PharmacyListResponse> findTop10Products() {
		// TODO Auto-generated method stub
		// Get the list of top 10 products
		List<ProductEntity> pharmacyList = pharmacyRepositoryImpl.findTop10Products();
		// Check if the list is not empty
		if (pharmacyList != null && !pharmacyList.isEmpty()) {
			// Convert to response list
			return toConverterPharmacy.toConverterPharmacyProductList(pharmacyList);
		}
		// If not found return empty list
		return new ArrayList<PharmacyListResponse>();
	}

	@Override
	public List<ProductViewProjection> findAllProductsWithFullInfo(Long id) {
		// TODO Auto-generated method stub
		// Get the list of products with full information
		List<ProductViewProjection> productViewProjections = pharmacyRepositoryImpl.findAllProductsWithFullInfo(id);
		// Check if the list is not empty
		if (productViewProjections != null && !productViewProjections.isEmpty()) {
			// Return the list of products with full information
			return productViewProjections;
		}
		// If not found return empty list
		return new ArrayList<ProductViewProjection>();
	}

	@Override
	public List<PharmacyListResponse> findRandomProductsByType(String productType) {
		// TODO Auto-generated method stub
		// Get the list of random products by type
		List<ProductEntity> pharmacyList = pharmacyRepositoryImpl.findRandomProductsByType(productType);
		// Check if the list is not empty
		if (pharmacyList != null && !pharmacyList.isEmpty()) {
			// Convert to response list
			return toConverterPharmacy.toConverterPharmacyProductList(pharmacyList);
		}
		// If not found return empty list
		return new ArrayList<PharmacyListResponse>();
	}

	@Override
	public Long countProducts() {
		// TODO Auto-generated method stub
		return pharmacyRepositoryImpl.countProducts();
	}

	@Override
	public ProductDetailDTO getProductDetailById(Long productId) {
		// TODO Auto-generated method stub
		// Check if productId is null or < 1
		if (productId == null || productId < 1) {
			// throw exception
			throw new IllegalArgumentException("Invalid product's id: " + productId);
		}
		// get the product by its id
		ProductEntity product = productRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("No product found with id " + productId));
		// new view dto
		ProductViewDTO viewDTO = new ProductViewDTO();
		// new detail dto
		ProductDetailDTO detailDTO = new ProductDetailDTO();
		// new set of category
		Set<String> categories = new LinkedHashSet<String>();
		// new map of additionalInfos
		Map<String, String> additionalInfos = new LinkedHashMap<String, String>();
		// set of tags
		Set<String> tags = new LinkedHashSet<String>();
		// List of reviews
		List<ReviewDTO> reviews = new ArrayList<ReviewDTO>();
		// set id
		viewDTO.setId(productId);
		// set type
		viewDTO.setType(product.getProductType());
		// set name
		viewDTO.setName(product.getName());
		// set description
		viewDTO.setDescription(product.getDescription());
		// set price
		viewDTO.setPrice(product.getPrice());
		// set unit
		viewDTO.setUnit(product.getUnit());
		// set status
		viewDTO.setProductStatus(product.getProductStatus());
		// set stock quantities
		viewDTO.setStockQuantities(product.getStockQuantities());
		// set image
		viewDTO.setImageUrl(product.getImageUrl());
		// set label
		viewDTO.setLabel(product.getLabel());
		// set product
		detailDTO.setProduct(viewDTO);
		// convert list of categories to set
		categories = categoryRepo.findCategoriesByProductId(productId).stream().map(CategoryResponse::getName)
				.filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
		// convert list of tags to set
		tags = tagRepo.findTagNamesByProductId(productId).stream().filter(Objects::nonNull)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		// convert list of additionalInfos to map
		additionalInfos = additionalInfoRepo.findAdditionalInfoByProductId(productId).stream()
				.filter(i -> i.getName() != null && i.getValue() != null)
				.collect(Collectors.toMap(ProductAdditionalInfoResponse::getName,
						ProductAdditionalInfoResponse::getValue, (v1, v2) -> v1, LinkedHashMap::new));
		// set lists of reviews
		reviews = reviewRepo.findReviewsByProductId(productId);
		// Check if reviees is empty or null
		if (reviews == null || reviews.isEmpty()) {
			ReviewDTO review = new ReviewDTO();
			review.setPatientFullName("Anonymous");
			review.setPatientImageUrl("/frontend_assets/assets/images/general/avatar.png");
			review.setContent("No reviews yet.");
			review.setRating(0);
			reviews = List.of(review);
		}
		// Get list of valid reviews
		List<ReviewDTO> validReviews = reviews.stream().filter(r -> r.getRating() != null).toList();
		// count review
		int reviewCount = validReviews.size();
		// calculate average rating
		double averageRating = reviewCount > 0
				? validReviews.stream().mapToDouble(ReviewDTO::getRating).average().orElse(0.0)
				: 0.0;
		// set categories
		detailDTO.setCategories(categories);
		// set tags
		detailDTO.setTags(tags);
		// set additional info
		detailDTO.setAdditionalInfos(additionalInfos);
		// set reviews
		detailDTO.setReviews(reviews);
		// set review count
		detailDTO.setReviewCount(reviewCount);
		// set average rating
		detailDTO.setAverageRating(averageRating);
		// get review count
		return detailDTO;
	}

	@Transactional
	@Override
	public void softDeleteById(Long id) {
		// TODO Auto-generated method stub
		// check if id is null or smaller than 1
		if (id == null || id < 1) {
			throw new IllegalArgumentException("Invalid product ID: " + id);
		}
		// Get product by id
		Optional<ProductEntity> optionalProduct = pharmacyRepositoryImpl.findById(id);
		// Check if optional is empty
		if (optionalProduct.isEmpty()) {
			throw new ResourceNotFoundException("No product found with ID : " + id);
		}
		// get product from optional
		ProductEntity product = optionalProduct.get();
		// Check if status is already INACTIVE
		if (product.getProductStatus() == ProductStatus.INACTIVE) {
			// display mesage
			System.out.println("Product is already INACTIVE.");
			return;
		}
		// set status
		product.setProductStatus(ProductStatus.INACTIVE);
		// save
		pharmacyRepositoryImpl.save(product);
	}

	@Override
	public ProductEntity saveFromDTO(ProductCreateDTO dto) {
		// TODO Auto-generated method stub
		// convert from the dto to entity
		ProductEntity entity = productCreateConverter.convertToEntity(dto);
		return pharmacyRepositoryImpl.save(entity);
	}

	@Override
	public void addCategoriesToProduct(ProductEntity product, Set<Long> categoryIds) {
		// TODO Auto-generated method stub
		for (Long categoryId : categoryIds) {
			CategoryEntity category = categoryRepo.findById(categoryId)
					.orElseThrow(() -> new RuntimeException("No category found with id : " + categoryId));
			// add product into category
			category.getProductEntities().add(product);
			// save category
			categoryRepo.save(category);
		}
	}

	@Override
	public ProductUpdateDTO getProductUpdateDetailById(Long productId) {
		// TODO Auto-generated method stub
		// Find product entity by id
		ProductEntity entity = pharmacyRepositoryImpl.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found."));
		// New dto
		ProductUpdateDTO dto = new ProductUpdateDTO();
		// convert from entity to dto
		dto = productUpdateConverter.convertToDTO(entity);
		// return dto
		return dto;
	}

	@Override
	public ProductEntity updateProductFromDTO(ProductUpdateDTO dto) {
		// TODO Auto-generated method stub
		// Check if dto is null
		if (dto == null) {
			throw new IllegalArgumentException("Product update DTO cannot be null");
		}
		// convert from dto to entity
		ProductEntity entity = productUpdateConverter.convertToEntity(dto);
		// Check if entity is null
		if (entity == null) {
			throw new IllegalArgumentException("Product entity cannot be null");
		}
		// Check if id is null or smaller than 1
		if (entity.getId() == null || entity.getId() < 1) {
			throw new IllegalArgumentException("Invalid product ID: " + entity.getId());
		}
		// Check if dto has valid categories
		if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
			// set 2-way (CategoryEntity is the owning side, ProductEntity is the inverse
			// side)
			this.addCategoriesToProduct(entity, dto.getCategoryIds());
		}
		//Delete old additional infos
		entity.getProductAdditionalInfoEntities().clear();
		//add new
		Set<ProductAdditionalInfoEntity> newInfos = productUpdateConverter.convertAdditionalInfosToEntities(entity,  dto.getAdditionalInfos());
		//set new infos
		entity.getProductAdditionalInfoEntities().addAll(newInfos);
		// save entity
		return pharmacyRepositoryImpl.save(entity);
	}

}
