package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.converter.ConverterPharmacyProduct;
import org.project.entity.ProductEntity;
import org.project.enums.ProductLabel;
import org.project.enums.Operation;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.project.utils.filter.SearchCriteria;
import org.project.utils.filter.FilterSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.project.enums.ProductSortType;
import org.project.model.response.PharmacyResponse;
import org.project.repository.ProductRepository;
import org.project.service.ProductService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of ProductService interface that provides comprehensive 
 * product management and search capabilities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ConverterPharmacyProduct converter;

    // ==================== Search and filtering ====================

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PharmacyResponse> searchProducts(Optional<String> searchQuery, Optional<Long> categoryId,
                                                 Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
                                                 Optional<String> type, ProductSortType sortType, Pageable pageable) {
        try {
            String sanitizedQuery = sanitizeSearchQuery(searchQuery);
            Optional<String> processedSearchQuery = sanitizedQuery != null ? Optional.of(sanitizedQuery) : Optional.empty();

            validateCommonInputs(pageable, minPrice, maxPrice, processedSearchQuery, type, categoryId);

            log.info("Searching products: searchQuery={}, categoryId={}, minPrice={}, maxPrice={}, type={}, sortType={}",
                    processedSearchQuery.orElse(""), categoryId.orElse(null), minPrice.orElse(null), maxPrice.orElse(null), type.orElse(""), sortType);

            Pageable sortedPageable = createSortedPageable(pageable, sortType);
            Specification<ProductEntity> spec = buildSpecification(processedSearchQuery, categoryId, minPrice, maxPrice, type);
            Page<ProductEntity> productPage = productRepository.findAll(spec, sortedPageable);
            Page<PharmacyResponse> resultPage = productPage.map(this::convertToDto);

            if (sortType == ProductSortType.RATING) {
                return applyRatingSortWithPagination(processedSearchQuery, categoryId, minPrice, maxPrice, type, pageable);
            }

            return applySorting(resultPage, sortType);
        } catch (Exception e) {
            log.error("Error searching products: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search products", e);
        }
    }

    /**
     * Apply rating-based sorting with pagination
     */
    private Page<PharmacyResponse> applyRatingSortWithPagination(
            Optional<String> searchQuery, Optional<Long> categoryId,
            Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
            Optional<String> type, Pageable pageable) {
        
        List<ProductEntity> allProducts = fetchAllProducts(searchQuery, categoryId, minPrice, maxPrice, type);
        if (allProducts.isEmpty()) {
            return Page.empty(pageable);
        }
        
        List<PharmacyResponse> allDtos = allProducts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
                
        List<PharmacyResponse> sortedContent = sortByRating(allDtos);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedContent.size());
        List<PharmacyResponse> paginatedContent = sortedContent.subList(start, end);
        
        return new PageImpl<>(paginatedContent, pageable, sortedContent.size());
    }

    // ==================== CRUD operations ====================

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findAll() {
        try {
            log.debug("Finding all products");
            return productRepository.findAll();
        } catch (Exception e) {
            log.error("Error finding all products: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * {@inheritDoc}
     */
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

    // ==================== Product display and fetching ====================

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponse> findTop10Products() {
        try {
            log.debug("Finding top 10 products");
            // Fetch first page without sorting by a non-existent column, then sort in memory by calculated rating
            List<ProductEntity> entities = new ArrayList<>(productRepository.findAll(PageRequest.of(0, 100)).getContent());
            // Sort in-memory
            entities.sort((e1, e2) -> Double.compare(calculateRating(e2), calculateRating(e1)));
            if (entities.size() > 10) {
                entities = entities.subList(0, 10);
            }
            return entities.stream().map(this::convertToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding top 10 products: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PharmacyResponse findProductById(Long id) {
        try {
            log.debug("Finding product DTO by ID: {}", id);
            return productRepository.findByProductStatusAndId(ProductStatus.ACTIVE, id)
                    .map(this::convertToDto)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Error finding product DTO by ID: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponse> findRelatedProducts(Long productId, int limit) {
        try {
            log.debug("Finding {} related products for product ID: {}", limit, productId);
            // Method to fetch related products based on category
            ProductEntity product = productRepository.findById(productId).orElse(null);
            if (product == null || product.getCategoryEntities() == null || product.getCategoryEntities().isEmpty()) {
                return new ArrayList<>();
            }
            Long categoryId = product.getCategoryEntities().iterator().next().getId();
            List<ProductEntity> relatedEntities = new ArrayList<>(
                productRepository.findByProductStatusAndCategoryEntities_Id(
                    ProductStatus.ACTIVE, categoryId, PageRequest.of(0, limit + 1)
                ).getContent()
            );
            // Sort in-memory by calculated rating
            relatedEntities.sort((e1, e2) -> Double.compare(calculateRating(e2), calculateRating(e1)));
            return relatedEntities.stream()
                    .filter(entity -> !entity.getId().equals(productId))
                    .limit(limit)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding related products: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * {@inheritDoc}
     */
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

    // ==================== Type-specific operations ====================

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findAllByProductType(ProductType productType) {
        try {
            log.debug("Finding all products by type: {}", productType);
            return productRepository.findAllByProductTypeAndProductStatus(productType, ProductStatus.ACTIVE);
        } catch (Exception e) {
            log.error("Error finding products by type: {}", e.getMessage(), e);
            return List.of();
        }
    }

    // ==================== Stock management ====================

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateStock(Long id, Integer quantity) {
        try {
            log.debug("Updating stock for product ID: {} to quantity: {}", id, quantity);
            ProductEntity product = findById(id);
            if (product != null) {
                product.setStockQuantities(quantity);
                productRepository.save(product);
            } else {
                log.warn("Cannot update stock: product with ID {} not found", id);
            }
        } catch (Exception e) {
            log.error("Error updating stock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update product stock", e);
        }
    }

    // ==================== Utility methods ====================

    /**
     * Sanitizes a search query by removing diacritical marks and special characters
     */
    private String sanitizeSearchQuery(Optional<String> searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return null;
        }

        // Step 1: trim and quick blank check
        String query = searchQuery.get().trim();
        if (query.isEmpty()) {
            return null;
        }

        // Bỏ dấu
        query = Normalizer.normalize(query, Normalizer.Form.NFD);
        query = query.replaceAll("\\p{M}", "");

        // Giữ chữ, số và space
        query = query.replaceAll("[^\\p{L}\\p{Nd}\\s]", " ");

        // Gom khoảng trắng và trim
        query = query.replaceAll("\\s+", " ").trim();

        return query.isEmpty() ? null : query.toLowerCase();
    }

    /**
     * Creates a sorted pageable based on the sort type
     */
    Pageable createSortedPageable(Pageable pageable, ProductSortType sortType) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
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

    /**
     * Determines the appropriate search strategy based on the provided criteria
     */
    private Page<ProductEntity> determineSearchStrategy(Optional<String> searchQuery, Optional<Long> categoryId,
                                                        Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
                                                        Optional<String> type, Pageable pageable) {
        try {
            if (searchQuery.isPresent() && !searchQuery.get().isEmpty()) {
                return searchByName(searchQuery.get(), pageable);
            }
            if (categoryId.isPresent()) {
                return searchByCategory(categoryId.get(), pageable);
            }
            if (minPrice.isPresent() || maxPrice.isPresent()) {
                return searchByPriceRange(minPrice.orElse(BigDecimal.ZERO), maxPrice.orElse(BigDecimal.valueOf(Long.MAX_VALUE)), pageable);
            }
            if (type.isPresent() && !type.get().trim().isEmpty()) {
                return searchByType(type.get().trim(), pageable);
            }
            return productRepository.findByProductStatus(ProductStatus.ACTIVE, pageable);
        } catch (Exception e) {
            log.error("Error determining search strategy: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    /**
     * Searches for products by name
     */
    @Deprecated // kept for compatibility; now uses Specification instead of LCS fuzzy search
    Page<ProductEntity> searchByName(String name, Pageable pageable) {
        validateCommonInputs(pageable, null, null, Optional.of(name), Optional.empty(), Optional.empty());
        try {
            Specification<ProductEntity> spec = new FilterSpecification<ProductEntity>()
                    .getSpecification("name", Operation.CONTAINS, name.toLowerCase());
            return productRepository.findAll(spec, pageable);
        } catch (Exception e) {
            log.error("Error searching by name: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    /**
     * Searches for products by category
     */
    private Page<ProductEntity> searchByCategory(Long categoryId, Pageable pageable) {
        validateCommonInputs(pageable, null, null, Optional.empty(), Optional.empty(), Optional.of(categoryId));
        try {
            return productRepository.findByProductStatusAndCategoryEntities_Id(
                ProductStatus.ACTIVE, categoryId, pageable);
        } catch (Exception e) {
            log.error("Error searching by category: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    /**
     * Searches for products within a price range
     */
    private Page<ProductEntity> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        validateCommonInputs(pageable, Optional.of(minPrice), Optional.of(maxPrice), Optional.empty(), Optional.empty(), Optional.empty());
        try {
            return productRepository.findByProductStatusAndPriceBetween(
                ProductStatus.ACTIVE, minPrice, maxPrice, pageable);
        } catch (Exception e) {
            log.error("Error searching by price range: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    /**
     * Searches for products by type or tag
     */
    private Page<ProductEntity> searchByType(String type, Pageable pageable) {
        if (type == null || type.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        String value = type.trim();
        
        try {
            // Try tag first
            Page<ProductEntity> tagResult = productRepository.findByProductStatusAndProductTagEntities_Id_NameIgnoreCase(
                ProductStatus.ACTIVE, value, pageable);
            
            if (!tagResult.isEmpty()) return tagResult;
            
            // Fallback label
            try {
                ProductLabel label = ProductLabel.valueOf(value.toUpperCase());
                return productRepository.findByProductStatusAndLabel(ProductStatus.ACTIVE, label, pageable);
            } catch (IllegalArgumentException ex) {
                log.debug("Value {} is not a valid ProductLabel", value);
                return Page.empty(pageable);
            }
        } catch (Exception e) {
            log.error("Error searching by type: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    /**
     * Fetches all products matching the specified criteria
     */
    private List<ProductEntity> fetchAllProducts(Optional<String> searchQuery, Optional<Long> categoryId,
                                                 Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
                                                 Optional<String> type) {
        String sanitizedQuery = sanitizeSearchQuery(searchQuery);
        Optional<String> processedSearchQuery = sanitizedQuery != null ? Optional.of(sanitizedQuery) : Optional.empty();
        try {
            Specification<ProductEntity> spec = buildSpecification(processedSearchQuery, categoryId, minPrice, maxPrice, type);
            return productRepository.findAll(spec, Sort.unsorted());
        } catch (Exception e) {
            log.error("Error fetching all products: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Applies sorting to a page of products based on the specified sort type
     */
    private Page<PharmacyResponse> applySorting(Page<PharmacyResponse> page, ProductSortType sortType) {
        if (page == null) {
            throw new IllegalArgumentException("Page cannot be null");
        }
        if (sortType == null || sortType == ProductSortType.DEFAULT) {
            return page;
        }
        if (page.getContent().isEmpty()) {
            return page;
        }
        List<PharmacyResponse> content = new ArrayList<>(page.getContent());
        switch (sortType) {
            case PRICE_ASC -> content.sort(Comparator.comparing(PharmacyResponse::getPrice, Comparator.nullsLast(Comparator.naturalOrder())));
            case PRICE_DESC -> content.sort(Comparator.comparing(PharmacyResponse::getPrice, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            default -> {}
        }
        return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    }

    /**
     * Converts a product entity to DTO
     */
    private PharmacyResponse convertToDto(ProductEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ProductEntity cannot be null");
        }
        if (converter == null) {
            throw new IllegalStateException("Converter is not initialized");
        }
        try {
            return converter.toDto(entity);
        } catch (Exception e) {
            log.error("Error converting ProductEntity to PharmacyResponse: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert product data", e);
        }
    }

    /**
     * Calculates the average rating for a product
     */
    private double calculateRating(ProductEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ProductEntity cannot be null");
        }
        if (entity.getReviewEntities() == null || entity.getReviewEntities().isEmpty()) {
            return 0.0;
        }
        try {
            return entity.getReviewEntities().stream()
                    .filter(review -> review.getRating() != null)
                    .mapToDouble(review -> review.getRating())
                    .average()
                    .orElse(0.0);
        } catch (Exception e) {
            log.error("Error calculating rating: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    /**
     * Sorts a list of products by rating
     */
    private List<PharmacyResponse> sortByRating(List<PharmacyResponse> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }
        List<PharmacyResponse> sortedProducts = new ArrayList<>(products);
        sortedProducts.sort(Comparator.comparing(PharmacyResponse::getRating, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return sortedProducts;
    }

    /**
     * Builds a specification for searching products based on the provided criteria
     */
    private Specification<ProductEntity> buildSpecification(Optional<String> searchQuery, Optional<Long> categoryId,
                                                            Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
                                                            Optional<String> type) {
        List<SearchCriteria> criteria = new ArrayList<>();
        searchQuery.ifPresent(q -> criteria.add(new SearchCriteria("name", Operation.CONTAINS, q)));
        categoryId.ifPresent(id -> criteria.add(new SearchCriteria("categoryEntities.id", Operation.EQUALS, id)));
        if (minPrice.isPresent() || maxPrice.isPresent()) {
            BigDecimal min = minPrice.orElse(BigDecimal.ZERO);
            BigDecimal max = maxPrice.orElse(BigDecimal.valueOf(Long.MAX_VALUE));
            criteria.add(new SearchCriteria("price", Operation.BETWEEN, new BigDecimal[]{min, max}));
        }
        type.filter(t -> !t.trim().isEmpty())
                .ifPresent(t -> criteria.add(new SearchCriteria("label", Operation.EQUALS, t.trim().toUpperCase())));
        return new FilterSpecification<ProductEntity>().getSpecifications(criteria);
    }

    /**
     * Validates the common input parameters for search operations
     */
    private void validateCommonInputs(Pageable pageable, Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
                                      Optional<String> searchQuery, Optional<String> type, Optional<Long> categoryId) {
        if (pageable != null) {
            if (pageable.getPageSize() <= 0 || pageable.getPageNumber() < 0) {
                throw new IllegalArgumentException("Invalid page size or page number");
            }
        }
        if (minPrice != null && minPrice.isPresent() && minPrice.get().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum price cannot be negative");
        }
        if (maxPrice != null && maxPrice.isPresent() && maxPrice.get().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maximum price cannot be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice.isPresent() && maxPrice.isPresent()) {
            if (minPrice.get().compareTo(maxPrice.get()) > 0) {
                throw new IllegalArgumentException("Minimum price must be less than or equal to maximum price");
            }
        }
        if (searchQuery != null && searchQuery.isPresent() && searchQuery.get().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty after sanitization");
        }
        if (type != null && type.isPresent()) {
            String value = type.get().trim();
            // Allow blank value to be treated as not provided
            if (!value.isEmpty()) {
                if (!value.matches("^[a-zA-Z0-9]+$")) {
                    throw new IllegalArgumentException("Product type contains invalid characters");
                }
            }
        }
        if (categoryId != null && categoryId.isPresent() && categoryId.get() <= 0) {
            throw new IllegalArgumentException("Invalid category ID");
        }
    }
}