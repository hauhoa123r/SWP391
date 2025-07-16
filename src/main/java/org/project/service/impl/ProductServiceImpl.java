package org.project.service.impl;

import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.project.converter.ConverterPharmacyProduct;
import org.project.entity.ProductEntity;
import org.project.enums.ProductSortType;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.project.enums.operation.ComparisonOperator;
import org.project.enums.operation.LogicalOperator;
import org.project.model.response.PharmacyResponse;
import org.project.repository.ProductRepository;
import org.project.service.ProductService;
import org.project.utils.specification.SpecificationUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.project.utils.specification.search.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Modern, Specification-based implementation of ProductService interface
 * that provides comprehensive product management and search capabilities.
 */
@Slf4j
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ConverterPharmacyProduct converter;
    private final ObjectProvider<SpecificationUtils<ProductEntity>> specUtilsProvider;

    public ProductServiceImpl(ProductRepository productRepository,
                              ConverterPharmacyProduct converter,
                              ObjectProvider<SpecificationUtils<ProductEntity>> specUtilsProvider) {
        this.productRepository = productRepository;
        this.converter = converter;
        this.specUtilsProvider = specUtilsProvider;
    }

    // ==================== Search & Filter ====================
    
    @Override
    @Transactional(readOnly = true)
    public Page<PharmacyResponse> searchProducts(Optional<String> searchQuery,
                                                 Optional<Long> categoryId,
                                                 Optional<BigDecimal> minPrice,
                                                 Optional<BigDecimal> maxPrice,
                                                 Optional<String> type,
                                                 ProductSortType sortType,
                                                 Pageable pageable) {
        try {
            String sanitized = sanitizeSearchQuery(searchQuery);
            Optional<String> processedQuery = Optional.ofNullable(sanitized);

            validateInputs(pageable, minPrice, maxPrice, categoryId);

            log.debug("Searching products: query={}, categoryId={}, minPrice={}, maxPrice={}, type={}, sortType={}",
                    processedQuery.orElse(""), categoryId.orElse(null), 
                    minPrice.orElse(null), maxPrice.orElse(null), type.orElse(""), sortType);

            Specification<ProductEntity> spec = buildSpecification(processedQuery, categoryId, minPrice, maxPrice, type);

            if (sortType == ProductSortType.RATING) {
                return applyRatingSortWithPagination(spec, pageable);
            }

            Pageable sortedPageable = createSortedPageable(pageable, sortType);
            Page<ProductEntity> entityPage = productRepository.findAll(spec, sortedPageable);
            List<ProductEntity> filtered = entityPage.getContent().stream()
                    .filter(p -> !belongsToTestCategory(p) && isAllowedType(p))
                    .collect(Collectors.toList());
            return new PageImpl<>(filtered.stream().map(this::convertToDto).collect(Collectors.toList()),
                    sortedPageable, entityPage.getTotalElements() - (entityPage.getContent().size() - filtered.size()));

        } catch (Exception e) {
            log.error("Error searching products: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search products", e);
        }
    }

    // ==================== CRUD Operations ====================

    @Override
    @Transactional
    public ProductEntity save(ProductEntity product) {
        try {
            log.debug("Saving product: {}", product.getName());
            return productRepository.save(product);
        } catch (Exception e) {
            log.error("Error saving product: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save product", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductEntity findById(Long id) {
        try {
            log.debug("Finding product by ID: {}", id);
            return productRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("Error finding product by ID: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findAll() {
        try {
            log.debug("Finding all products");
            return productRepository.findAll().stream()
                    .filter(p -> !belongsToTestCategory(p) && isAllowedType(p))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding all products: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try {
            log.debug("Deleting product by ID: {}", id);
            productRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting product by ID: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    // ==================== Product Display & Fetching ====================

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponse> findTop10Products() {
        try {
            log.debug("Finding top 10 products");
            List<ProductEntity> entities = productRepository.findAll(PageRequest.of(0, 100)).getContent()
                    .stream()
                    .filter(p -> !belongsToTestCategory(p) && isAllowedType(p))
                    .collect(Collectors.toList());
            return entities.stream()
                    .sorted(Comparator.comparingDouble(this::calculateRating).reversed())
                    .limit(10)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding top 10 products: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PharmacyResponse findProductById(Long id) {
        try {
            log.debug("Finding product DTO by ID: {}", id);
            return productRepository.findByProductStatusAndId(ProductStatus.ACTIVE, id)
                     .filter(p -> !belongsToTestCategory(p) && isAllowedType(p))
                     .map(this::convertToDto)
                     .orElse(null);
        } catch (Exception e) {
            log.error("Error finding product DTO by ID: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponse> findRelatedProducts(Long productId, int limit) {
        try {
            log.debug("Finding {} related products for product ID: {}", limit, productId);
            ProductEntity base = productRepository.findById(productId).orElse(null);
            if (base == null || base.getCategoryEntities() == null || base.getCategoryEntities().isEmpty()) {
                return List.of();
            }
            
            Long categoryId = base.getCategoryEntities().iterator().next().getId();
            List<ProductEntity> related = productRepository
                     .findByProductStatusAndCategoryEntities_Id(ProductStatus.ACTIVE, categoryId,
                             PageRequest.of(0, limit + 1)).getContent()
                     .stream().filter(p -> !belongsToTestCategory(p) && isAllowedType(p)).collect(Collectors.toList());
            
            return related.stream()
                    .filter(p -> !p.getId().equals(productId))
                    .sorted(Comparator.comparingDouble(this::calculateRating).reversed())
                    .limit(limit)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding related products: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long findFirstProductId() {
        try {
            log.debug("Finding first product ID");
            return productRepository.findFirstByProductStatusOrderById(ProductStatus.ACTIVE);
        } catch (Exception e) {
            log.error("Error finding first product ID: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findAllByProductType(ProductType productType) {
        try {
            log.debug("Finding all products by type: {}", productType);
            return productRepository.findAllByProductTypeAndProductStatus(productType, ProductStatus.ACTIVE)
                    .stream()
                    .filter(p -> !belongsToTestCategory(p) && isAllowedType(p))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding products by type: {}", e.getMessage(), e);
            return List.of();
        }
    }

    // ==================== Stock Management ====================

    @Override
    @Transactional
    public void updateStock(Long id, Integer quantity) {
        try {
            log.debug("Updating stock for product ID: {} to quantity: {}", id, quantity);
            productRepository.findById(id).ifPresentOrElse(
                    product -> {
                        product.setStockQuantities(quantity);
                        productRepository.save(product);
                    },
                    () -> log.warn("Cannot update stock: product with ID {} not found", id)
            );
        } catch (Exception e) {
            log.error("Error updating stock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update product stock", e);
        }
    }

    // ==================== Internal Helper Methods ====================

    private boolean belongsToTestCategory(ProductEntity entity) {
        if (entity == null || entity.getCategoryEntities() == null) {
            return false;
        }
        return entity.getCategoryEntities().stream()
                .anyMatch(c -> "TEST".equalsIgnoreCase(c.getName()));
    }

    private boolean isAllowedType(ProductEntity entity) {
        if (entity == null) {
            return false;
        }
        ProductType type = entity.getProductType();
        return type == ProductType.MEDICINE || type == ProductType.MEDICAL_PRODUCT;
    }

    private PharmacyResponse convertToDto(ProductEntity entity) {
        return converter.toDto(entity);
    }

    private Specification<ProductEntity> buildSpecification(Optional<String> searchQuery,
                                                             Optional<Long> categoryId,
                                                             Optional<BigDecimal> minPrice,
                                                             Optional<BigDecimal> maxPrice,
                                                             Optional<String> type) {
        SpecificationUtils<ProductEntity> util = specUtilsProvider.getObject().reset();

        // Add search query criteria
        searchQuery.ifPresent(q -> util.addSearchCriteria(
                SearchCriteria.builder()
                        .fieldName("name")
                        .comparisonOperator(ComparisonOperator.CONTAINS)
                        .comparedValue(q)
                        .joinType(JoinType.INNER)
                        .build(), LogicalOperator.AND));

        // Exclude category TEST
        util.addSearchCriteria(SearchCriteria.builder()
                .fieldName("categoryEntities.name")
                .comparisonOperator(ComparisonOperator.NOT_EQUALS)
                .comparedValue("TEST")
                .joinType(JoinType.INNER)
                .build(), LogicalOperator.AND);

        // Add category criteria
        categoryId.ifPresent(id -> util.addSearchCriteria(
                SearchCriteria.builder()
                        .fieldName("categoryEntities.id")
                        .comparisonOperator(ComparisonOperator.EQUALS)
                        .comparedValue(id)
                        .joinType(JoinType.INNER)
                        .build(), LogicalOperator.AND));

        // Add price range criteria
        if (minPrice.isPresent() || maxPrice.isPresent()) {
            BigDecimal min = minPrice.orElse(BigDecimal.ZERO);
            BigDecimal max = maxPrice.orElse(BigDecimal.valueOf(Long.MAX_VALUE));
            util.addSearchCriteria(SearchCriteria.builder()
                            .fieldName("price")
                            .comparisonOperator(ComparisonOperator.BETWEEN)
                            .comparedValue(new BigDecimal[]{min, max})
                            .joinType(JoinType.INNER)
                            .build(), LogicalOperator.AND);
        }

        // Add type criteria
        type.filter(t -> !t.isBlank()).ifPresent(t -> util.addSearchCriteria(
                SearchCriteria.builder()
                        .fieldName("label")
                        .comparisonOperator(ComparisonOperator.EQUALS)
                        .comparedValue(t.trim().toUpperCase())
                        .joinType(JoinType.INNER)
                        .build(), LogicalOperator.AND));

        return util.getSpecification();
    }

    private Page<PharmacyResponse> applyRatingSortWithPagination(Specification<ProductEntity> spec, Pageable pageable) {
        List<PharmacyResponse> sorted = productRepository.findAll(spec).stream()
                .map(this::convertToDto)
                .sorted(Comparator.comparing(PharmacyResponse::getRating,
                        Comparator.nullsLast(Double::compareTo)).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sorted.size());
        List<PharmacyResponse> subList = start >= sorted.size() ? List.of() : sorted.subList(start, end);
        return new PageImpl<>(subList, pageable, sorted.size());
    }

    private Pageable createSortedPageable(Pageable pageable, ProductSortType sortType) {
        if (sortType == null || sortType == ProductSortType.DEFAULT || sortType == ProductSortType.RATING) {
            return pageable;
        }
        Sort sort = switch (sortType) {
            case PRICE_ASC -> Sort.by("price").ascending();
            case PRICE_DESC -> Sort.by("price").descending();
            default -> Sort.unsorted();
        };
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    private double calculateRating(ProductEntity entity) {
        if (entity == null || entity.getReviewEntities() == null || entity.getReviewEntities().isEmpty()) {
            return 0.0;
        }
        return entity.getReviewEntities().stream()
                .filter(r -> r.getRating() != null)
                .mapToDouble(r -> r.getRating())
                .average()
                .orElse(0.0);
    }

    private String sanitizeSearchQuery(Optional<String> queryOpt) {
        if (queryOpt == null || queryOpt.isEmpty()) {
            return null;
        }
        
        String query = queryOpt.get().trim();
        if (query.isEmpty()) {
            return null;
        }

        // Remove diacritical marks
        query = Normalizer.normalize(query, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^\\p{L}\\p{Nd}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return query.isBlank() ? null : query.toLowerCase();
    }

    private void validateInputs(Pageable pageable, Optional<BigDecimal> minPrice, 
                               Optional<BigDecimal> maxPrice, Optional<Long> categoryId) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        
        if (pageable.getPageSize() <= 0 || pageable.getPageNumber() < 0) {
            throw new IllegalArgumentException("Invalid pageable parameters");
        }
        
        if (minPrice.isPresent() && maxPrice.isPresent() && minPrice.get().compareTo(maxPrice.get()) > 0) {
            throw new IllegalArgumentException("minPrice must be <= maxPrice");
        }
        
        if (categoryId.isPresent() && categoryId.get() <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
    }
}